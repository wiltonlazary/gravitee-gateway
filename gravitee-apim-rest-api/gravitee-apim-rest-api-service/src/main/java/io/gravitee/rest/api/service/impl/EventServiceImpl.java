/**
 * Copyright (C) 2015 The Gravitee team (http://gravitee.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.gravitee.rest.api.service.impl;

import static io.gravitee.repository.management.model.Event.EventProperties.API_ID;
import static io.gravitee.repository.management.model.Event.EventProperties.ID;
import static io.gravitee.rest.api.model.PlanStatus.CLOSED;
import static java.util.stream.Collectors.toSet;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.springframework.util.CollectionUtils.isEmpty;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.gravitee.common.data.domain.Page;
import io.gravitee.definition.model.debug.DebugApi;
import io.gravitee.repository.exceptions.TechnicalException;
import io.gravitee.repository.management.api.EventRepository;
import io.gravitee.repository.management.api.search.EventCriteria;
import io.gravitee.repository.management.api.search.builder.PageableBuilder;
import io.gravitee.repository.management.model.Api;
import io.gravitee.repository.management.model.Dictionary;
import io.gravitee.repository.management.model.Event;
import io.gravitee.repository.management.model.flow.FlowReferenceType;
import io.gravitee.rest.api.model.*;
import io.gravitee.rest.api.service.EventService;
import io.gravitee.rest.api.service.PlanService;
import io.gravitee.rest.api.service.UserService;
import io.gravitee.rest.api.service.common.ExecutionContext;
import io.gravitee.rest.api.service.common.UuidString;
import io.gravitee.rest.api.service.configuration.flow.FlowService;
import io.gravitee.rest.api.service.converter.PlanConverter;
import io.gravitee.rest.api.service.exceptions.EventNotFoundException;
import io.gravitee.rest.api.service.exceptions.TechnicalManagementException;
import io.gravitee.rest.api.service.exceptions.UserNotFoundException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * @author Titouan COMPIEGNE
 */
@Component
public class EventServiceImpl extends TransactionalService implements EventService {

    private final Logger LOGGER = LoggerFactory.getLogger(EventServiceImpl.class);

    @Lazy
    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PlanService planService;

    @Autowired
    private FlowService flowService;

    @Autowired
    private PlanConverter planConverter;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public EventEntity findById(ExecutionContext executionContext, String id) {
        try {
            LOGGER.debug("Find event by ID: {}", id);

            Optional<Event> event = eventRepository.findById(id);

            if (event.isPresent()) {
                return convert(executionContext, event.get());
            }

            throw new EventNotFoundException(id);
        } catch (TechnicalException ex) {
            LOGGER.error("An error occurs while trying to find an event using its ID {}", id, ex);
            throw new TechnicalManagementException("An error occurs while trying to find an event using its ID " + id, ex);
        }
    }

    @Override
    public EventEntity createApiEvent(
        ExecutionContext executionContext,
        Set<String> environmentsIds,
        EventType type,
        Api api,
        Map<String, String> properties
    ) {
        Api apiDefinition = api != null ? buildApiEventPayload(executionContext, api) : null;
        return createEvent(executionContext, environmentsIds, type, apiDefinition, properties);
    }

    @Override
    public EventEntity createDebugApiEvent(
        ExecutionContext executionContext,
        Set<String> environmentsIds,
        EventType type,
        DebugApi debugApi,
        Map<String, String> properties
    ) {
        return createEvent(executionContext, environmentsIds, type, debugApi, properties);
    }

    @Override
    public EventEntity createDictionaryEvent(
        ExecutionContext executionContext,
        Set<String> environmentsIds,
        EventType type,
        Dictionary dictionary,
        Map<String, String> properties
    ) {
        return createEvent(executionContext, environmentsIds, type, dictionary, properties);
    }

    @Override
    public EventEntity createOrganizationEvent(
        ExecutionContext executionContext,
        Set<String> environmentsIds,
        EventType type,
        OrganizationEntity organization,
        Map<String, String> properties
    ) {
        return createEvent(executionContext, environmentsIds, type, organization, properties);
    }

    private EventEntity createEvent(
        ExecutionContext executionContext,
        final Set<String> environmentsIds,
        EventType type,
        Object object,
        Map<String, String> properties
    ) {
        try {
            String payload = object != null ? objectMapper.writeValueAsString(object) : null;
            NewEventEntity event = NewEventEntity.builder().type(type).payload(payload).properties(properties).build();
            return createNewEventEntity(executionContext, environmentsIds, event);
        } catch (JsonProcessingException e) {
            throw new TechnicalManagementException(String.format("Failed to create event [%s]", type), e);
        }
    }

    protected EventEntity createNewEventEntity(
        ExecutionContext executionContext,
        final Set<String> environmentsIds,
        NewEventEntity newEventEntity
    ) {
        String hostAddress = "";
        try {
            hostAddress = InetAddress.getLocalHost().getHostAddress();
            LOGGER.debug("Create {} for server {}", newEventEntity, hostAddress);

            Event event = convert(newEventEntity);
            event.setId(UuidString.generateRandom());
            event.setEnvironments(environmentsIds);
            // Set origin
            event.getProperties().put(Event.EventProperties.ORIGIN.getValue(), hostAddress);
            // Set date fields
            event.setCreatedAt(new Date());
            event.setUpdatedAt(event.getCreatedAt());

            Event createdEvent = eventRepository.create(event);

            return convert(executionContext, createdEvent);
        } catch (UnknownHostException e) {
            LOGGER.error("An error occurs while getting the server IP address", e);
            throw new TechnicalManagementException("An error occurs while getting the server IP address", e);
        } catch (TechnicalException ex) {
            LOGGER.error("An error occurs while trying to create {} for server {}", newEventEntity, hostAddress, ex);
            throw new TechnicalManagementException(
                "An error occurs while trying create " + newEventEntity + " for server " + hostAddress,
                ex
            );
        }
    }

    @Override
    public void delete(String eventId) {
        try {
            LOGGER.debug("Delete Event {}", eventId);
            eventRepository.delete(eventId);
        } catch (TechnicalException ex) {
            LOGGER.error("An error occurs while trying to delete Event {}", eventId, ex);
            throw new TechnicalManagementException("An error occurs while trying to delete Event " + eventId, ex);
        }
    }

    @Override
    public Page<EventEntity> search(
        ExecutionContext executionContext,
        List<EventType> eventTypes,
        Map<String, Object> properties,
        long from,
        long to,
        int page,
        int size,
        final List<String> environmentsIds
    ) {
        EventCriteria.Builder builder = new EventCriteria.Builder().from(from).to(to);

        if (eventTypes != null) {
            io.gravitee.repository.management.model.EventType[] eventTypesArr = eventTypes
                .stream()
                .map(eventType -> io.gravitee.repository.management.model.EventType.valueOf(eventType.toString()))
                .toArray(io.gravitee.repository.management.model.EventType[]::new);

            builder.types(eventTypesArr);
        }

        if (properties != null) {
            properties.forEach(builder::property);
        }

        builder.environments(environmentsIds);

        Page<Event> pageEvent = eventRepository.search(builder.build(), new PageableBuilder().pageNumber(page).pageSize(size).build());

        List<EventEntity> content = pageEvent
            .getContent()
            .stream()
            .map(event -> convert(executionContext, event))
            .collect(Collectors.toList());

        return new Page<>(content, pageEvent.getPageNumber(), (int) pageEvent.getPageElements(), pageEvent.getTotalElements());
    }

    @Override
    public <T> Page<T> search(
        ExecutionContext executionContext,
        List<EventType> eventTypes,
        Map<String, Object> properties,
        long from,
        long to,
        int page,
        int size,
        Function<EventEntity, T> mapper,
        final List<String> environmentsIds
    ) {
        return search(executionContext, eventTypes, properties, from, to, page, size, mapper, (T t) -> true, environmentsIds);
    }

    @Override
    public <T> Page<T> search(
        ExecutionContext executionContext,
        List<EventType> eventTypes,
        Map<String, Object> properties,
        long from,
        long to,
        int page,
        int size,
        Function<EventEntity, T> mapper,
        Predicate<T> filter,
        final List<String> environmentsIds
    ) {
        Page<EventEntity> result = search(executionContext, eventTypes, properties, from, to, page, size, environmentsIds);
        return new Page<>(
            result.getContent().stream().map(mapper).filter(filter).collect(Collectors.toList()),
            page,
            size,
            result.getTotalElements()
        );
    }

    @Override
    public Collection<EventEntity> search(ExecutionContext executionContext, final EventQuery query) {
        LOGGER.debug("Search APIs by {}", query);
        return convert(executionContext, eventRepository.search(queryToCriteria(executionContext, query).build()));
    }

    private EventCriteria.Builder queryToCriteria(ExecutionContext executionContext, EventQuery query) {
        final EventCriteria.Builder builder = new EventCriteria.Builder()
        // FIXME: Move this environments filter to EventQuery
            .environments(Collections.singletonList(executionContext.getEnvironmentId()));
        if (query == null) {
            return builder;
        }
        builder.from(query.getFrom()).to(query.getTo());

        if (!isEmpty(query.getTypes())) {
            query
                .getTypes()
                .forEach(eventType -> builder.types(io.gravitee.repository.management.model.EventType.valueOf(eventType.name())));
        }

        if (!isEmpty(query.getProperties())) {
            query.getProperties().forEach(builder::property);
        }

        if (!isBlank(query.getApi())) {
            builder.property(API_ID.getValue(), query.getApi());
        }

        if (!isBlank(query.getId())) {
            builder.property(ID.getValue(), query.getId());
        }

        return builder;
    }

    private Set<EventEntity> convert(ExecutionContext executionContext, List<Event> events) {
        return events.stream().map(event -> convert(executionContext, event)).collect(toSet());
    }

    private EventEntity convert(ExecutionContext executionContext, Event event) {
        EventEntity eventEntity = new EventEntity();
        eventEntity.setId(event.getId());
        eventEntity.setType(io.gravitee.rest.api.model.EventType.valueOf(event.getType().toString()));
        eventEntity.setPayload(event.getPayload());
        eventEntity.setParentId(event.getParentId());
        eventEntity.setProperties(event.getProperties());
        eventEntity.setCreatedAt(event.getCreatedAt());
        eventEntity.setUpdatedAt(event.getUpdatedAt());
        eventEntity.setEnvironments(event.getEnvironments());

        if (event.getProperties() != null) {
            final String userId = event.getProperties().get(Event.EventProperties.USER.getValue());
            if (userId != null && !userId.isEmpty()) {
                try {
                    eventEntity.setUser(userService.findById(executionContext, userId));
                } catch (UserNotFoundException unfe) {
                    UserEntity user = new UserEntity();
                    user.setSource("system");
                    user.setId(userId);
                    eventEntity.setUser(user);
                }
            }
        }

        return eventEntity;
    }

    private Event convert(NewEventEntity newEventEntity) {
        Event event = new Event();
        event.setType(io.gravitee.repository.management.model.EventType.valueOf(newEventEntity.getType().toString()));
        event.setPayload(newEventEntity.getPayload());
        event.setParentId(newEventEntity.getParentId());
        event.setProperties(new HashMap<>(newEventEntity.getProperties()));

        return event;
    }

    /**
     * Build gateway API event payload for given API.
     *
     * @param executionContext
     * @param api
     * @return Gateway API event payload
     * @throws JsonProcessingException
     */
    private Api buildApiEventPayload(ExecutionContext executionContext, Api api) {
        try {
            Api apiForGatewayEvent = new Api(api);
            apiForGatewayEvent.setDefinition(objectMapper.writeValueAsString(buildGatewayApiDefinition(executionContext, api)));
            return apiForGatewayEvent;
        } catch (JsonProcessingException e) {
            throw new TechnicalManagementException(String.format("Failed to build API [%s] definition for gateway event", api.getId()), e);
        }
    }

    /**
     * Build gateway API definition for given Api.
     *
     * It reads API plans from plan collections, and API flows from flow collection ;
     * And generates gateway API definition from management API definition (containing no plans or flows).
     *
     * @param executionContext
     * @param api
     * @return API definition
     * @throws JsonProcessingException
     */
    private io.gravitee.definition.model.Api buildGatewayApiDefinition(ExecutionContext executionContext, Api api)
        throws JsonProcessingException {
        var apiDefinition = objectMapper.readValue(api.getDefinition(), io.gravitee.definition.model.Api.class);

        Set<PlanEntity> plans = planService
            .findByApi(executionContext, api.getId())
            .stream()
            .filter(p -> p.getStatus() != CLOSED)
            .collect(toSet());

        apiDefinition.setPlans(planConverter.toPlansDefinitions(plans));
        apiDefinition.setFlows(flowService.findByReference(FlowReferenceType.API, api.getId()));
        return apiDefinition;
    }
}
