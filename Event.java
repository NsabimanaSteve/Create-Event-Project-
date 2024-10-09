import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Represents an event with a title, start and end times, location, ID, description, and priority.
 */
public class Event {
    private String title;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String location;
    private String id;
    private String description;
    private String priority;

    /**
     * Constructs an Event with the specified details.
     *
     * @param title       the title of the event
     * @param startTime   the start time of the event
     * @param endTime     the end time of the event
     * @param location    the location of the event
     * @param id          the unique identifier for the event
     * @param description a description of the event
     * @param priority    the priority level of the event
     * @throws IllegalArgumentException if the end time is before the start time
     */
    public Event(String title, LocalDateTime startTime, LocalDateTime endTime, String location, String id, String description, String priority) {
        this.title = title;
        this.location = location;
        this.id = id;
        this.description = description;
        this.priority = priority;
        setStartAndEndTime(startTime, endTime);
    }

    /**
     * Sets the start and end times for the event.
     *
     * @param startTime the start time of the event
     * @param endTime   the end time of the event
     * @throws IllegalArgumentException if the end time is before the start time
     */
    public void setStartAndEndTime(LocalDateTime startTime, LocalDateTime endTime) {
        if (endTime.isBefore(startTime)) {
            throw new IllegalArgumentException("End time cannot be before start time");
        }
        this.startTime = startTime;
        this.endTime = endTime;
    }

    /**
     * Returns the title of the event.
     *
     * @return the title of the event
     */
    public String getTitle() {
        return title;
    }

    /**
     * Returns the start time of the event.
     *
     * @return the start time of the event
     */
    public LocalDateTime getStartTime() {
        return startTime;
    }

    /**
     * Returns the end time of the event.
     *
     * @return the end time of the event
     */
    public LocalDateTime getEndTime() {
        return endTime;
    }

    /**
     * Returns the location of the event.
     *
     * @return the location of the event
     */
    public String getLocation() {
        return location;
    }

    /**
     * Returns the unique identifier of the event.
     *
     * @return the unique identifier of the event
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the description of the event.
     *
     * @return the description of the event
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the priority of the event.
     *
     * @return the priority of the event
     */
    public String getPriority() {
        return priority;
    }

    /**
     * Returns a string representation of the event.
     *
     * @return a string representation of the event
     */
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy h:mm a");
        return String.format("Event Title: %s\nStart Time: %s\nEnd Time: %s\nLocation: %s\nID: %s\nDescription: %s\nPriority: %s\n",
                title, startTime.format(formatter), endTime.format(formatter), location, id, description, priority);
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * @param o the reference object with which to compare
     * @return {@code true} if this object is the same as the obj argument; {@code false} otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return Objects.equals(id, event.id);
    }

    /**
     * Returns a hash code value for the object.
     *
     * @return a hash code value for this object
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
