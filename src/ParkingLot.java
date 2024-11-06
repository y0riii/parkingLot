import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.CountDownLatch;

public class ParkingLot {

    private final int TOTAL_SPOTS = 4;
    private final Semaphore parkingSpots = new Semaphore(TOTAL_SPOTS, true);
    private final AtomicInteger carsCurrentlyParked = new AtomicInteger(0);
    private final AtomicInteger totalCarsServed = new AtomicInteger(0);
    private final AtomicInteger[] gateCarsServed = new AtomicInteger[] { new AtomicInteger(0), new AtomicInteger(0),
            new AtomicInteger(0) };
    private final AtomicInteger lastLeavingTime = new AtomicInteger(0);

    // This latch ensures that all car threads start simultaneously
    private final CountDownLatch latch = new CountDownLatch(1);

    public void enter(int gateNumber, int carId, int arrivalTime) {
        try {
            System.out.printf("Car %d from Gate %d arrived at time %d\n", carId, gateNumber, arrivalTime);
            boolean didWait = false;
            if (!parkingSpots.tryAcquire()) {
                didWait = true;
                System.out.printf("Car %d from Gate %d waiting for a spot.\n", carId, gateNumber);
                // Block until a spot is available
                parkingSpots.acquire();
            }
            carsCurrentlyParked.incrementAndGet();
            gateCarsServed[gateNumber - 1].incrementAndGet();
            totalCarsServed.incrementAndGet();
            if (didWait && lastLeavingTime.get() - arrivalTime > 0) {
                System.out.printf(
                        "Car %d from Gate %d parked after waiting for %d units of time. (Parking Status: %d spots occupied)\n",
                        carId, gateNumber, lastLeavingTime.get() - arrivalTime, carsCurrentlyParked.get());
            } else {
                System.out.printf("Car %d from Gate %d parked. (Parking Status: %d spots occupied)\n",
                        carId, gateNumber, carsCurrentlyParked.get());
            }
        } catch (InterruptedException e) {
            System.out.printf("Car %d from Gate %d was interrupted.\n", carId, gateNumber);
        }
    }

    public void exit(int gateNumber, int carId, int arrivalTime, int duration) {
        carsCurrentlyParked.decrementAndGet();
        lastLeavingTime.set(arrivalTime + duration + 1);
        System.out.printf("Car %d from Gate %d left after %d units of time. (Parking Status: %d spots occupied)\n",
                carId, gateNumber, duration, carsCurrentlyParked.get());
        parkingSpots.release();
    }

    // Method to print the summary after the simulation
    public void displaySummary() {
        System.out.println("\nSimulation completed.");
        System.out.printf("Total Cars Served: %d\n", totalCarsServed.get());
        System.out.printf("Current Cars in Parking: %d\n", carsCurrentlyParked.get());
        System.out.println("Details:");
        for (int i = 0; i < gateCarsServed.length; i++) {
            System.out.printf("- Gate %d served %d cars.\n", i + 1, gateCarsServed[i].get());
        }
    }

    // Getter for the latch, so Car class can use it to synchronize
    public CountDownLatch getLatch() {
        return latch;
    }
}