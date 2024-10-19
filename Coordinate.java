public class Coordinate
{
    public static final char BOMB = 'B';
    public static final char EMPTY = 'E';
    public static final char FLAG = 'F';
    public static final char UNREVEALED = '#';

    /**
     * Status of the coordinate.
     * @implNote Helps to determine if we should reveal the 
     * coordinate or not to the client.
     */
    public enum Status{ UNREVEALED, REVEALED, FLAGGED }
    private int x;
    private int y;
    private char value;
    private Status status;

    /**
     * Constructor for the Coordinate class.
     * @param x The x coordinate of the cell.
     * @param y The y coordinate of the cell.
     * @param value The value of the cell.
     * @param status The status of the cell.
     */
    public Coordinate(int x, int y, char value, Status status)
    {
        this.x = x;
        this.y = y;
        this.value = value;
        this.status = status;
    }

    /**
     * Set the value of the coordinate.
     * @param value The value of the coordinate.
     */
    public void setCoordinateValue(char value)
    {
        this.value = value;
    }

    /**
     * Set the status of the coordinate.
     * @param status The status of the coordinate.
     */
    public void setCoordinateStatus(Status status)
    {
        this.status = status;
    }

    /**
     * Get the value of the coordinate.
     * @return The value of the coordinate.
     */
    public char getCoordinateValue()
    {
        return value;
    }

    /**
     * Get the status of the coordinate.
     * @return The status of the coordinate.
     */
    public Status getCoordinateStatus()
    {
        return status;
    }
}
