import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.CountDownLatch;

public class ParkingLot {

    public static int TOTAL_SPOTS = 4;
    public static Semaphore parkingSpots = new Semaphore(TOTAL_SPOTS, true);
    public static AtomicInteger carsCurrentlyParked = new AtomicInteger(0);
    public static AtomicInteger totalCarsServed = new AtomicInteger(0);
    public static AtomicInteger[] gateCarsServed = new AtomicInteger[]{new AtomicInteger(0), new AtomicInteger(0), new AtomicInteger(0)};
    
    // This latch ensures that all car threads start simultaneously
    private static final CountDownLatch latch;

    static {
        latch = new CountDownLatch(1); // Initialize latch
    }

    public static void main(String[] args) {
        String inputFilePath = "input.txt";  // Path to the input file
        long endTime = 0;
        
        try (BufferedReader br = new BufferedReader(new FileReader(inputFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] carData = line.split(", ");
                int gateNumber = Integer.parseInt(carData[0].split(" ")[1]);
                int carId = Integer.parseInt(carData[1].split(" ")[1]);
                int arrivalTime = Integer.parseInt(carData[2].split(" ")[1]);
                int duration = Integer.parseInt(carData[3].split(" ")[1]);
                endTime = Math.max(endTime, arrivalTime + duration);
                
                // Create new Car thread
                Car car = new Car(gateNumber, carId, arrivalTime, duration);
                new Thread(car).start();
            }
            latch.countDown();
        } catch (IOException e) {
            System.err.println("Error reading the input file.");
            e.printStackTrace();
        }

        // Wait for the simulation to finish before printing the final summary
        try {
            // Wait for the final car to leave.
            Thread.sleep(endTime * 1000 + 10000); 
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Print the final summary after all threads have finished their task
        displaySummary();
    }

    // Method to print the summary after the simulation
    public static void displaySummary() {
        System.out.println("\nSimulation completed.");
        System.out.printf("Total Cars Served: %d\n", totalCarsServed.get());
        System.out.printf("Current Cars in Parking: %d\n", carsCurrentlyParked.get());
        System.out.println("Details:");
        for (int i = 0; i < gateCarsServed.length; i++) {
            System.out.printf("- Gate %d served %d cars.\n", i + 1, gateCarsServed[i].get());
        }
    }
    
    // Getter for the latch, so Car class can use it to synchronize
    public static CountDownLatch getLatch() {
        return latch;
    }
}