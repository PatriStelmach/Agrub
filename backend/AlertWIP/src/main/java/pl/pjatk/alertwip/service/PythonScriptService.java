package pl.pjatk.alertwip.service;
import org.springframework.stereotype.Service;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

@Service
public class PythonScriptService {

    public void runScript(String scriptName, String... args) {
        try {
            // Przygotowanie komendy
            String[] command = new String[args.length + 2];
            command[0] = "python3";
            command[1] = "scripts/" + scriptName;
            System.arraycopy(args, 0, command, 2, args.length);

            ProcessBuilder pb = new ProcessBuilder(command);
            pb.redirectErrorStream(true);
            Process process = pb.start();

            // Odczyt logów w czasie rzeczywistym
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("[PYTHON][" + scriptName + "]: " + line);
                }
            }

            // Oczekiwanie na zakończenie z timeoutem
            boolean exited = process.waitFor(30, TimeUnit.SECONDS);
            if (!exited) {
                process.destroyForcibly();
                System.err.println("TIMEOUT: Skrypt " + scriptName + " został ubity.");
            }

        } catch (Exception e) {
            System.err.println("BŁĄD KRYTYCZNY: " + e.getMessage());
        }
    }
}