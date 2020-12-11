public class IntegerLockOnValue extends IntegerLock{

    private int lockValue;

    IntegerLockOnValue(int value, int lockValue) {
        super(value);
        this.lockValue = lockValue;
    }

    public synchronized void lockOnValue() throws InterruptedException {
        while (locked || value == lockValue) {
            wait();
        }
        locked = true;
    }

}
