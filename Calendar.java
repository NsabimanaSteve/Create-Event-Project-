import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

/**
 * Manages a collection of events, providing functionality to add, remove, update, view, and sort events.
 * It also maintains a history of past events.
 */
public class Calendar {
    private Map<String, Event> events;
    private Map<String, Event> history;

    /**
     * Constructs a new CalendarManager with empty events and history maps.
     */
    public Calendar() {
        events = new HashMap<>();
        history = new HashMap<>();
    }

    /**
     * Adds a new event to the calendar if there is no conflict with existing events.
     *
     * @param newEvent the event to add
     * @return true if the event was added successfully, false if there is a conflict
     */
    public boolean addEvent(Event newEvent) {
        // Check for conflicts with existing events
        for (Event existingEvent : events.values()) {
            if (eventsOverlap(existingEvent, newEvent)) {
                return false; // Conflict found
            }
        }

        // No conflicts, add the event
        events.put(newEvent.getStartTime().toString(), newEvent);
        return true;
    }

    /**
     * Checks if two events overlap in time.
     *
     * @param first the first event
     * @param second the second event
     * @return true if the events overlap, false otherwise
     */
    private boolean eventsOverlap(Event first, Event second) {
        return !(first.getEndTime().isBefore(second.getStartTime()) ||
                first.getStartTime().isAfter(second.getEndTime()));
    }

    /**
     * Removes an event from the calendar based on the specified key.
     *
     * @param key the key of the event to remove
     */
    public void removeEvent(String key) {
        if (events.remove(key) != null) {
            System.out.println("Event removed successfully.");
        } else {
            System.out.println("No event found for the given date and time.");
        }
    }

    /**
     * Updates an event in the calendar based on user input.
     *
     * @param key     the key of the event to update
     * @param scanner the scanner to read user input
     */
    public void updateEvent(String key, Scanner scanner) {
        Event event = events.get(key);
        if (event == null) {
            System.out.println("No event found for the given date and time.");
            return;
        }

        System.out.print("Enter new title (or press Enter to keep the current): ");
        String newTitle = scanner.nextLine().trim();
        if (newTitle.isEmpty()) {
            newTitle = event.getTitle();
        }

        System.out.print("Enter new description (or press Enter to keep the current): ");
        String newDescription = scanner.nextLine().trim();
        if (newDescription.isEmpty()) {
            newDescription = event.getDescription();
        }

        System.out.print("Enter new location (or press Enter to keep the current): ");
        String newLocation = scanner.nextLine().trim();
        if (newLocation.isEmpty()) {
            newLocation = event.getLocation();
        }

        System.out.print("Enter new priority (or press Enter to keep the current): ");
        String newPriority = scanner.nextLine().trim();
        if (newPriority.isEmpty()) {
            newPriority = event.getPriority();
        }

        // Update start and end times with validation
        LocalDateTime newStartTime = event.getStartTime();
        LocalDateTime newEndTime = event.getEndTime();

        System.out.print("Enter new start time (MM/DD/YYYY HH:MM AM/PM) (or press Enter to keep the current): ");
        String newStartTimeStr = scanner.nextLine().trim();
        if (!newStartTimeStr.isEmpty()) {
            try {
                newStartTime = LocalDateTime.parse(newStartTimeStr, DateTimeFormatter.ofPattern("MM/dd/yyyy h:mm a"));
            } catch (DateTimeParseException e) {
                System.out.println("Invalid start time format. Keeping the current start time.");
            }
        }

        System.out.print("Enter new end time (MM/DD/YYYY HH:MM AM/PM) (or press Enter to keep the current): ");
        String newEndTimeStr = scanner.nextLine().trim();
        if (!newEndTimeStr.isEmpty()) {
            try {
                newEndTime = LocalDateTime.parse(newEndTimeStr, DateTimeFormatter.ofPattern("MM/dd/yyyy h:mm a"));
                if (newEndTime.isBefore(newStartTime)) {
                    System.out.println("End time must be after start time. Keeping the current end time.");
                    newEndTime = event.getEndTime();
                }
            } catch (DateTimeParseException e) {
                System.out.println("Invalid end time format. Keeping the current end time.");
            }
        }

        Event updatedEvent = new Event(newTitle, newStartTime, newEndTime, newLocation, event.getId(), newDescription, newPriority);
        events.remove(key);
        events.put(newStartTime.toString(), updatedEvent);
        System.out.println("Event updated successfully.");
    }

    /**
     * Displays all events sorted by their start time.
     */
    public void displayAllEvents() {
        List<Event> sortedEvents = new ArrayList<>(events.values());
        sortedEvents.sort(Comparator.comparing(Event::getStartTime));
        for (Event event : sortedEvents) {
            System.out.println(event);
        }
    }

    /**
     * Filters events based on a specified attribute and filter value.
     *
     * @param attribute  the attribute to filter by (title, location, priority, description, date)
     * @param filterValue the value to filter by
     * @return a list of events that match the filter criteria
     */
    public List<Event> viewEvents(String attribute, String filterValue) {
        List<Event> filteredEvents = new ArrayList<>();
        for (Event event : events.values()) {
            switch (attribute.toLowerCase()) {
                case "title":
                    if (event.getTitle().toLowerCase().contains(filterValue.toLowerCase())) {
                        filteredEvents.add(event);
                    }
                    break;
                case "location":
                    if (event.getLocation().toLowerCase().contains(filterValue.toLowerCase())) {
                        filteredEvents.add(event);
                    }
                    break;
                case "priority":
                    if (event.getPriority().toLowerCase().contains(filterValue.toLowerCase())) {
                        filteredEvents.add(event);
                    }
                    break;
                case "description":
                    if (event.getDescription().toLowerCase().contains(filterValue.toLowerCase())) {
                        filteredEvents.add(event);
                    }
                    break;
                case "date":
                    try {
                        LocalDate filterDate = LocalDate.parse(filterValue, DateTimeFormatter.ofPattern("MM/dd/yyyy"));
                        if (event.getStartTime().toLocalDate().isEqual(filterDate)) {
                            filteredEvents.add(event);
                        }
                    } catch (DateTimeParseException e) {
                        System.out.println("Invalid date format. Please use MM/DD/YYYY.");
                    }
                    break;
                default:
                    System.out.println("Invalid attribute.");
                    return Collections.emptyList();
            }
        }
        filteredEvents.sort(Comparator.comparing(Event::getStartTime));
        return filteredEvents;
    }

    /**
     * Sorts events based on a specified attribute using the quicksort algorithm.
     *
     * @param attribute the attribute to sort by (date, title, priority)
     * @return a list of sorted events
     */
    public List<Event> sortEvents(String attribute) {
        List<Event> sortedEvents = new ArrayList<>(events.values());
        if (attribute.equalsIgnoreCase("date")) {
            quicksort(sortedEvents, Comparator.comparing(Event::getStartTime), 0, sortedEvents.size() - 1);
        } else if (attribute.equalsIgnoreCase("title")) {
            quicksort(sortedEvents, Comparator.comparing(Event::getTitle), 0, sortedEvents.size() - 1);
        } else if (attribute.equalsIgnoreCase("priority")) {
            quicksort(sortedEvents, Comparator.comparing(Event::getPriority), 0, sortedEvents.size() - 1);
        }
        return sortedEvents;
    }

    /**
     * Performs the quicksort algorithm on a list.
     *
     * @param list       the list to sort
     * @param comparator the comparator to determine the order of the list
     * @param low        the starting index
     * @param high       the ending index
     * @param <T>        the type of elements in the list
     */
    private <T> void quicksort(List<T> list, Comparator<? super T> comparator, int low, int high) {
        if (low < high) {
            int pi = partition(list, comparator, low, high);
            quicksort(list, comparator, low, pi - 1);
            quicksort(list, comparator, pi + 1, high);
        }
    }

    /**
     * Partitions the list for the quicksort algorithm.
     *
     * @param list       the list to partition
     * @param comparator the comparator to determine the order
     * @param low        the starting index
     * @param high       the ending index
     * @param <T>        the type of elements in the list
     * @return the partition index
     */
    private <T> int partition(List<T> list, Comparator<? super T> comparator, int low, int high) {
        T pivot = list.get(high);
        int i = (low - 1);
        for (int j = low; j < high; j++) {
            if (comparator.compare(list.get(j), pivot) <= 0) {
                i++;
                Collections.swap(list, i, j);
            }
        }
        Collections.swap(list, i + 1, high);
        return i + 1;
    }

    /**
     * Searches for an event by its start date and time.
     *
     * @param dateTime the start date and time of the event
     * @return the event if found, null otherwise
     */
    public Event searchEventByDatetime(LocalDateTime dateTime) {
        return events.get(dateTime.toString());
    }

    /**
     * Moves past events from the active list to the history list.
     */
    public void refreshEvents() {
        LocalDateTime now = LocalDateTime.now();
        List<String> keysToMove = new ArrayList<>();

        for (Map.Entry<String, Event> entry : events.entrySet()) {
            if (entry.getValue().getEndTime().isBefore(now)) {
                keysToMove.add(entry.getKey());
            }
        }

        for (String key : keysToMove) {
            Event event = events.remove(key);
            history.put(key, event);
        }
    }

    /**
     * Retrieves a list of all past events.
     *
     * @return a list of past events
     */
    public List<Event> getHistoryEvents() {
        return new ArrayList<>(history.values());
    }

    /**
     * Generates a summary of events within a specified date range.
     *
     * @param startDate the start date of the range
     * @param endDate   the end date of the range
     * @return a summary of events in the range
     */
    public String generateSummary(LocalDate startDate, LocalDate endDate) {
        StringBuilder summary = new StringBuilder();
        for (Event event : events.values()) {
            if (!event.getStartTime().toLocalDate().isBefore(startDate) && !event.getEndTime().toLocalDate().isAfter(endDate)) {
                summary.append(event.toString()).append("\n\n");
            }
        }
        for (Event event : history.values()) {
            if (!event.getStartTime().toLocalDate().isBefore(startDate) && !event.getEndTime().toLocalDate().isAfter(endDate)) {
                summary.append(event.toString()).append("\n\n");
            }
        }
        return summary.toString();
    }
}
