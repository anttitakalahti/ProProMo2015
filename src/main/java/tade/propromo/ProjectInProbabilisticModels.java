package tade.propromo;

import java.util.Scanner;

/**
 * Project in Probabilistic Models
 * https://www.cs.helsinki.fi/en/courses/582637/2015/k/k/1
 *
 * @author Antti Takalahti 012092323
 */
public class ProjectInProbabilisticModels {

    private static String refineryId;
    private static Worker worker;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("HELLO\t1.0");

        worker = new Worker();

        while (true) {
            String command = scanner.nextLine();
            String[] parts = command.split("\\t");
            String currentRequestId = parts[1];

            if (command.startsWith("SETUP\t")) {

                refineryId = parts[1];
                System.out.println("ALIVE\t" + refineryId + "\tinfo=Project in Probabilistic Models by Antti Takalahti\tversion=0.003");

            } else if (command.startsWith("PERFORM\t") && !refineryId.isEmpty()) {
                String currentContextId = parts[2];
                String currentWork      = parts[3];
                String currentInput     = parts[4];
                worker.updateData(currentRequestId, currentContextId, currentWork, currentInput);
                worker.run();
                /* TODO run here is silly. You should keep previous values here and
                 *      use thread.start() so that we can abort if the need comes.
                 */
                System.out.println("READY\t" + currentRequestId + "\t" + currentWork + "/output/output.csv");

            } else if (command.startsWith("ABORT\t") && !refineryId.isEmpty()) {

                worker.cancel();

            } else if (command.startsWith("PING\t")) {

                System.out.println("PONG\t" + parts[1]);

            } else {

                System.err.println("Uhhuh!\nReceived: " + command);
                System.exit(0);

            }
        }
    }
}
