public class IntegerLock {

    public int value;
    boolean locked = false;

    IntegerLock(int value){
        this.value = value;
    }

    public synchronized void lock() throws InterruptedException {
        while (locked) {
            wait();
        }
        locked = true;
    }

    public synchronized void unlock() {
        locked = false;
        notifyAll();
    }

}
