package cz.vity.freerapid.sandbox;

/**
 * @author Vity
 */
public class TestCircularBuffer {
    public static void main(String args[]) {
        TestCircularBuffer t = new TestCircularBuffer();
    }

    public TestCircularBuffer() {
        CircularBuffer c = new CircularBuffer(8);

        System.out.println("Storing: 1");
        c.store(1);
        System.out.println("Reading: " + c.read());
        System.out.println("Storing: 2");
        c.store(2);
        System.out.println("Storing: 3");
        c.store(3);
        System.out.println("Storing: 4");
        c.store(4);
        System.out.println("Reading: " + c.read());
        System.out.println("Reading: " + c.read());
        System.out.println("Storing: 8");
        c.store(8);
        System.out.println("Storing: 9");
        c.store(9);
        System.out.println("Storing: 10");
        c.store(10);
        System.out.println("Storing: 11");
        c.store(11);
        System.out.println("Storing: 12");
        c.store(12);
        System.out.println("Reading: " + c.read());
        System.out.println("Reading: " + c.read());
        System.out.println("Reading: " + c.read());
        System.out.println("Reading: " + c.read());
        System.out.println("Reading: " + c.read());
        System.out.println("Reading: " + c.read());
        System.out.println("Reading: " + c.read());
        System.out.println("Reading: " + c.read());
    }
}

class CircularBuffer {
    private Integer data[];
    private int head;
    private int tail;

    public CircularBuffer(Integer number) {
        data = new Integer[number];
        head = 0;
        tail = 0;
    }

    public boolean store(Integer value) {
        if (!bufferFull()) {
            data[tail++] = value;
            if (tail == data.length) {
                tail = 0;
            }
            return true;
        } else {
            return false;
        }
    }

    public Integer read() {
        if (head != tail) {
            int value = data[head++];
            if (head == data.length) {
                head = 0;
            }
            return value;
        } else {
            return null;
        }
    }

    private boolean bufferFull() {
        if (tail + 1 == head) {
            return true;
        }
        if (tail == (data.length - 1) && head == 0) {
            return true;
        }
        return false;
    }
}