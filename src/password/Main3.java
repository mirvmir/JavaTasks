package password;

import java.security.SecureRandom;
import java.util.*;


class PasswordGenerator {
    private static final String upCase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String lowCase = "abcdefghijklmnopqrstuvwxyz";
    private static final String numbers = "0123456789";
    private static final String symbols = "!@#$%^&?*.";
    private static final String all = upCase + lowCase + numbers + symbols;
    private static final SecureRandom rnd = new SecureRandom();

    public static String generatePassword(int length) {
        List<Character> passwordChars = new ArrayList<>();

        passwordChars.add(upCase.charAt(rnd.nextInt(upCase.length())));
        passwordChars.add(lowCase.charAt(rnd.nextInt(lowCase.length())));
        passwordChars.add(numbers.charAt(rnd.nextInt(numbers.length())));
        passwordChars.add(symbols.charAt(rnd.nextInt(symbols.length())));

        for (int i = 0; i < length - 4; i++) {
            passwordChars.add(all.charAt(rnd.nextInt(all.length())));
        }

        Collections.shuffle(passwordChars, rnd);

        StringBuilder sb = new StringBuilder();
        for (char c : passwordChars) {
            sb.append(c);
        }

        return sb.toString();
    }
}

interface Command {
    void execute();
}

class Input implements Command {
    private final Scanner scanner = new Scanner(System.in);
    AppData data = AppData.getInstance();

    @Override
    public void execute() {
        int length = 0;
        while (length < 8 || length > 12) {
            System.out.print("Введите длину пароля (8-12): ");
            try {
                length = scanner.nextInt();
                if (length < 8 || length > 12) {
                    System.out.println("Ожидается чисто от 8 до 12.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Ожидается целое число.");
                scanner.nextLine();
            }
        }
        data.setLength(length);
        data.setPassword();
    }
}

class Output implements Command {
    AppData data = AppData.getInstance();

    @Override
    public void execute() {
        System.out.println("Ваш пароль: " + data.getPassword());
    }
}

class AppData {
    private static AppData instance;
    private String password;
    private Integer length;

    public static AppData getInstance() {
        if (instance == null) {
            instance = new AppData();
        }
        return instance;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword() {
        this.password = PasswordGenerator.generatePassword(length);
    }

    public void setLength(Integer length) {
        this.length = length;
    }
}

class AppLoop {
    private final List<Command> commands = new ArrayList<>();

    public AppLoop() {
        commands.add(new Input());
        commands.add(new Output());
    }

    public void run() {
        for (Command command : commands) {
            command.execute();
        }
    }
}

public class Main3 {
    public static void main(String[] args) {
        AppLoop app = new AppLoop();
        app.run();
    }
}
