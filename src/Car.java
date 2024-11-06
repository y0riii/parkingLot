public class Car implements Runnable {
    private final int gateNumber;
    private final int carId;
    private final int arrivalTime;
    private final int duration;
    private final ParkingLot parkingLot;

    public Car(int gateNumber, int carId, int arrivalTime, int duration, ParkingLot parkingLot) {
        this.gateNumber = gateNumber;
        this.carId = carId;
        this.arrivalTime = arrivalTime;
        this.duration = duration;
        this.parkingLot = parkingLot;
    }

    @Override
    public void run() {
        try {
            // Waiting for the latch from ParkingLot so all threads start at the same time
            parkingLot.getLatch().await();

            // Simulate arrival time
            Thread.sleep(arrivalTime * 1000L);
            int waitedTime = parkingLot.enter(gateNumber, carId, arrivalTime);

            // Simulate parking duration
            Thread.sleep(duration * 1000L);
            parkingLot.exit(gateNumber, carId, arrivalTime + waitedTime, duration);
        } catch (InterruptedException e) {
            System.out.printf("Car %d from Gate %d was interrupted.\n", carId, gateNumber);
        }
    }
}