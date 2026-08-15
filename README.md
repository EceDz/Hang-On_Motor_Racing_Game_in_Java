# Hang-On: Motor Racing Game

A single player motorbike racer game implemented with a graphical user interface. Every user is required to login prior to playing the game, with user details stored in a separate text file. During the game, the user is able to control the player using keys, with separate buttons to pause or restart the game. Bot players have random starting positions, and there is a checkpoint line which the player is supposed to reach within a timeframe. Depending on the game time, the player earns points. If the player goes out of bounds, a Fall Animation is shown, and if the time is over, a time over screen is shown. After every game, the player's score is saved with a timestamp.

> This was a coursework project — the full assignment brief is preserved in [`Contents`](Contents). I received 103 out of 100 with bonus points and got the second highest score overall in the class.

## Features

- **Graphical user interface** built entirely with Java Swing, including a welcome/login screen and an in-game HUD.
- **User accounts** — players register a username/password and must log in before playing; credentials are persisted to a text file.
- **Procedurally curving road** with a checkpoint system: reach the next checkpoint within the time limit to keep racing.
- **AI bot racers** (7 of them) that move along the track and can be avoided/overtaken.
- **Speed, score, and countdown timer** displayed live on the game screen.
- **Pause / restart controls** available during play.
- **Crash / fall animation** when the player's bike goes off the track.
- **Sound effects and music** — engine sound that responds to speed, a crash sound, a beep, and background music.
- **Persistent high-score board** with timestamps, sorted by score.

## Repository Structure

```
.
├── Code/
│   ├── Main.java              # Entry point — launches the login screen
│   ├── LoginScreen.java       # Welcome window, registration & login UI
│   ├── GameScreen.java        # Core game loop UI: HUD, controls, timers, sound, game state
│   ├── GameThread.java        # Game loop / rendering thread
│   ├── Motorbike.java         # Base class for rideable/AI bikes
│   ├── Player.java            # Player-controlled motorbike (input, position, animation state)
│   ├── Bot.java                # AI-controlled competitor motorbikes
│   ├── FallAnimation.java     # Handles the "off track" crash animation sequence
│   ├── RoadManager.java       # Track/road generation, curves, checkpoints, scrolling
│   ├── RoadSegment.java       # Enum/model for road segment types (e.g. straight, curve)
│   ├── User.java               # User model (username/password)
│   ├── UserReader.java        # Reads/writes user accounts to users.txt
│   ├── ScoreboardManager.java # Reads/writes/sorts high scores to scoreboard.txt
│   ├── users.txt               # Persisted user credentials
│   ├── scoreboard.txt          # Persisted high scores (name:score:timestamp)
│   ├── *.png                   # Sprites (player, bots, fall animation frames, logo)
│   └── *.wav                   # Sound effects and music (engine, crash, beep, soundtrack)
├── Contents                    # Original assignment brief/requirements
├── LICENSE
└── README.md
```

## How to Run

The project is plain Java Swing with no external dependencies or build tool — it can be compiled and run directly with the JDK, or opened as-is in an IDE (e.g. Eclipse/IntelliJ).

1. Make sure you have a JDK installed (Java 8+).
2. Clone the repository and move into the `Code` directory:
   ```bash
   git clone https://github.com/EceDz/Hang-On_Motor_Racing_Game.git
   cd Hang-On_Motor_Racing_Game/Code
   ```
3. Compile the source:
   ```bash
   javac -d out *.java
   ```
4. Run the game (image/sound resources are loaded from the classpath, so run from inside `Code`, or make sure `Code` is on the classpath):
   ```bash
   java -cp .:out Project.Main      # Linux/macOS
   java -cp .;out Project.Main      # Windows
   ```

> **Note:** `UserReader` and `ScoreboardManager` read/write `src/Project/users.txt` and `src/Project/scoreboard.txt` relative to the working directory. If you set up the project in an IDE with that `src/Project/...` folder layout (as it was originally developed), user accounts and scores will persist correctly out of the box; otherwise adjust the paths or working directory to match.

## How to Play

1. From the welcome screen, **register** a new account (User → Register) or **log in** with existing credentials (User → Login).
2. Start the race (Game → Start). You'll control your bike with:
   - **Left / Right arrows** — steer
   - **Up / Down arrows** — accelerate / brake
   - **Esc** or the on-screen buttons — pause
3. Reach each checkpoint before the timer runs out to keep going and rack up points based on your time.
4. Avoid the 7 AI bots and stay on the track — going off-road triggers a crash/fall animation and ends the run.
5. After the game ends, your score is saved with a timestamp; check **User → High Score** to see the leaderboard.

## License

This project is licensed under the [MIT License](LICENSE).
