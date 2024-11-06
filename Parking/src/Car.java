public class Car implements Runnable {
    private final int gateNumber;
    private final int carId;
    private final int arrivalTime;
    private final int duration;

    public Car(int gateNumber, int carId, int arrivalTime, int duration) {
        this.gateNumber = gateNumber;
        this.carId = carId;
        this.arrivalTime = arrivalTime;
        this.duration = duration;
    }

    @Override
    public void run() {
        try {
        	// Waiting for the latch from parkingLot so all threads start at the same time
        	ParkingLot.getLatch().await();
        	
            // Simulate arrival time
        	boolean waited = false;
            Thread.sleep(arrivalTime * 1000);
            System.out.printf("Car %d from Gate %d arrived at time %d\n", carId, gateNumber + 1, arrivalTime);

            double waitStartTime = System.currentTimeMillis();
            if (!ParkingLot.parkingSpots.tryAcquire()) {
            	waited = true;
                System.out.printf("Car %d from Gate %d waiting for a spot.\n", carId, gateNumber + 1);
                // Block until a spot is available
                ParkingLot.parkingSpots.acquire();
            }
            double waitEndTime = System.currentTimeMillis();
            double waitingTime = Math.round((waitEndTime - waitStartTime) / 1000) + 1;
            ParkingLot.carsCurrentlyParked.incrementAndGet();
            ParkingLot.gateCarsServed[gateNumber].incrementAndGet();
            ParkingLot.totalCarsServed.incrementAndGet();
            if (waited && waitEndTime - waitStartTime > 0) {
                System.out.printf("Car %d from Gate %d parked after waiting for %d units of time. (Parking Status: %d spots occupied)\n",
                        carId, gateNumber + 1, (int) waitingTime, ParkingLot.carsCurrentlyParked.get());
            } else {                    	
            	System.out.printf("Car %d from Gate %d parked. (Parking Status: %d spots occupied)\n", 
            			carId, gateNumber + 1, ParkingLot.carsCurrentlyParked.get());
            }
            
            // Simulate parking duration
            Thread.sleep(duration * 1000);
            
            // Car leaves
            ParkingLot.carsCurrentlyParked.decrementAndGet();
            System.out.printf("Car %d from Gate %d left after %d units of time. (Parking Status: %d spots occupied)\n", 
            		carId, gateNumber + 1, duration, ParkingLot.carsCurrentlyParked.get());
            ParkingLot.parkingSpots.release();
            

            

        } catch (InterruptedException e) {
            System.out.printf("Car %d from Gate %d was interrupted.\n", carId, gateNumber + 1);
        }
    }
}
