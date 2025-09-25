package hangman;

import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;


class Game {
    private static Game instance;
    private GameState state;
    private String word;
    private Set<Character> guessedLetters = new HashSet<>();
    private int attemptsLeft = 6;

    private static String[] words = {
            "джава", "игра", "виселица", "программа", "коленкор"
    };

    private static String[] consoleView = {
            """
        ┌──────────────────┐
        │                  │
        │                  │
        │                  │
        │                  │
        │                  │
        │                  │
        │                  │
        │                  │
        │                  │
        │                  │
        └──────────────────┘""",
            """
        ┌──────────────────┐
        │                  │
        │                  │
        │                  │
        │                  │
        │                  │
        │                  │
        │                  │
        │                  │
        │                  │
        │ ---------------- │
        └──────────────────┘""",
            """
        ┌──────────────────┐
        │                  │
        │                | │
        │                | │
        │                | │
        │                | │
        │                | │
        │                | │
        │                | │
        │                | │
        │ ---------------- │
        └──────────────────┘""",
            """
        ┌──────────────────┐
        │      +--------+  │
        │                | │
        │                | │
        │                | │
        │                | │
        │                | │
        │                | │
        │                | │
        │                | │
        │ ---------------- │
        └──────────────────┘""",
            """
        ┌──────────────────┐
        │      +--------+  │
        │      |         | │
        │      |         | │
        │      |         | │
        │      |_        | │
        │      | |       | │
        │      |_|       | │
        │                | │
        │                | │
        │ ---------------- │
        └──────────────────┘""",
            """
        ┌──────────────────┐
        │      +--------+  │
        │      |         | │
        │      |         | │
        │      |         | │
        │ ／￣￣＼|       | │
        │ |  ´0｡ |＼     | │
        │ |    Д |  ＼   | │
        │ |  、0｡|     ＼ | │
        │ ＼＿＿／(U ＿ U )| │
        │          '-'-' | │
        │ ---------------- │
        └──────────────────┘""",
            """
        ┌──────────────────┐
        │      +--------+  │
        │      |     ^v^ | │
        │  ^v^ |         | │
        │      |         | │
        │ ／￣￣＼|       | │
        │ |  ´x  |＼     | │
        │ |    p |  ＼   | │
        │ |  、x |     ＼ | │
        │ ＼＿＿／(U ＿ U )| │
        │          '-'-' | │
        │ ---------------- │
        └──────────────────┘"""
    };

    private Game() {}

    public static Game getInstance() {
        if (instance == null) {
            instance = new Game();
        }
        return instance;
    }

    public void setState(GameState state) {
        this.state = state;
        state.handle();
    }

    public GameState getState() {
        return state;
    }

    public void init(String word) {
        this.word = word.toLowerCase();
        this.guessedLetters.clear();
        this.attemptsLeft = 6;
        setState(new PlayingState(this));
    }

    public String getWord() { return word; }
    public Set<Character> getGuessedLetters() { return guessedLetters; }
    public int getAttemptsLeft() { return attemptsLeft; }
    public void decreaseAttempts() { attemptsLeft--; }

    public void printHangman() {
        System.out.println("===== ВИСЕЛИЦА =====");
        int stage = 6 - attemptsLeft;
        if (stage < 0) stage = 0;
        if (stage >= consoleView.length) stage = consoleView.length - 1;
        System.out.println(consoleView[stage]);
    }

    public String getRandomWord() {
        Random rnd = new Random();
        return words[rnd.nextInt(words.length)];
    }
}


abstract class GameState {
    protected Game game;
    public GameState(Game game) { this.game = game; }
    public abstract void handle();
}

class PlayingState extends GameState {
    public PlayingState(Game game) { super(game); }

    public void handle() {
        game.printHangman();

        String masked = game.getWord().replaceAll(".", "_");

        for (int i = 0; i < game.getWord().length(); i++) {
            char ch = game.getWord().charAt(i);
            if (game.getGuessedLetters().contains(ch)) {
                masked = masked.substring(0, i) + ch + masked.substring(i + 1);
            }
        }

        System.out.println("Слово: " + masked);
        System.out.println("Осталось попыток: " + game.getAttemptsLeft());

        if (masked.replace(" ", "").equals(game.getWord())) {
            game.setState(new WinState(game));
        } else if (game.getAttemptsLeft() <= 0) {
            game.setState(new LoseState(game));
        }
    }
}

class WinState extends GameState {
    public WinState(Game game) { super(game); }
    public void handle() {
        System.out.println("WINNER.");
    }
}

class LoseState extends GameState {
    public LoseState(Game game) { super(game); }
    public void handle() {
        System.out.println("GAME OVER.\nСлово было " + game.getWord());
    }
}


interface Command {
    void execute();
}

class GuessLetterCommand implements Command {
    private char letter;
    public GuessLetterCommand(char letter) { this.letter = letter; }

    @Override
    public void execute() {
        Game game = Game.getInstance();
        if (game.getState() instanceof PlayingState) {
            if (!game.getGuessedLetters().contains(letter)) {
                if (game.getWord().contains(String.valueOf(letter))) {
                    game.getGuessedLetters().add(letter);
                    System.out.println("Такая буква есть.");
                } else {
                    game.getGuessedLetters().add(letter);
                    game.decreaseAttempts();
                    System.out.println("Такой буквы нет.");
                }
            }
            else  System.out.println("Буква уже открыта.");
            game.setState(new PlayingState(game));
        }
    }
}


class GameLoop {
    protected Game game = Game.getInstance();
    protected Scanner scanner = new Scanner(System.in);
    private Command lastCommand;

    public final void run() {
        init();
        while (game.getState() instanceof PlayingState) {
            input();
            update();
        }
        end();
    }

    protected void init() {
        game.init(game.getRandomWord());
    }

    protected void input() {
        char letter = ' ';
        while (true) {
            System.out.println("Введите букву из русского алфавита: ");
            String in = scanner.nextLine();
            if (in.length() == 1) {
                letter = in.toLowerCase().charAt(0);
                if (letter >= 'а' && letter <= 'я'  || letter == 'ё') break;
            }
        }
        setCommand(new GuessLetterCommand(letter));
    }

    protected void update() {
        executeCommand();
    }

    protected void end() {
        System.out.println("Игра завершена.");
    }

    protected void setCommand(Command cmd) {
        this.lastCommand = cmd;
    }

    protected void executeCommand() {
        if (lastCommand != null) lastCommand.execute();
    }
}


public class Main1 {
    public static void main(String[] args) {
        new GameLoop().run();
    }
}
