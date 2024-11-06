import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Main {
    private final static ParkingLot parkingLot = new ParkingLot();

    public static void main(String[] args) {
        String inputFilePath = "input.txt"; // Path to the input file
        ArrayList<Thread> carThreads = new ArrayList<>();


        try (BufferedReader br = new BufferedReader(new FileReader(inputFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                Thread carThread = getCarThread(line);
                carThreads.add(carThread);
                carThread.start();
            }
            parkingLot.getLatch().countDown();
        } catch (IOException e) {
            System.err.println("Error reading the input file.");
            e.printStackTrace();
        }

        // Wait for the simulation to finish before printing the final summary
        for (Thread carThread : carThreads) {
            try {
                carThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Print the final summary after all threads have finished their task
        parkingLot.displaySummary();
    }

    private static Thread getCarThread(String line) {
        String[] carData = line.split(", ");
        int gateNumber = Integer.parseInt(carData[0].split(" ")[1]);
        int carId = Integer.parseInt(carData[1].split(" ")[1]);
        int arrivalTime = Integer.parseInt(carData[2].split(" ")[1]);
        int duration = Integer.parseInt(carData[3].split(" ")[1]);

        // Create new Car thread
        Car car = new Car(gateNumber, carId, arrivalTime, duration, parkingLot);
        return new Thread(car);
    }
}
