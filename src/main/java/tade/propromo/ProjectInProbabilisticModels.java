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

            if (command.startsWith("SETUP\t")) {

                refineryId = parts[1];
                System.out.println("ALIVE\t" + refineryId + "\tinfo=Project in Probabilistic Models by Antti Takalahti\tversion=0.001");

            } else if (command.startsWith("PERFORM\t") && !refineryId.isEmpty()) {

                worker.updateData(command);
                worker.run();

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
