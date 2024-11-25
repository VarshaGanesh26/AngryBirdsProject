# AngryBirdsProject

This is an Angry Birds project, a clone of the popular Angry Birds game, built using the LibGDX framework. The game features a static GUI of all screens with a proper flow. Sprites have also been used to create the level screen. The project is organized into different packages for easy navigation and maintenance.

1) Main
This is the entry point of the game that initializes LibGDX and manages screen transitions. Constants V_WIDTH and V_HEIGHT have been defined in this class.

2) Screens
a) HomeScreen
Play button to start the game.
Settings button for game configuration.
Quit button to exit the game.

We have used labels to display text and texture to display the background image.

b) SettingsScreen
T&C to view terms and conditions.
Volume toggle (plays the Angry Birds theme song).
Exit settings and return to HomeScreen.

c) LevelScreen
Three level options (Levels 1, 2, and 3).
Back button to return to HomeScreen.
Persistent music controls.

d) LevelOne
This is the first level of the game with a simple block structure.
Pause on top-left to stop playing temporarily.
You can choose to win or lose, as this is only a static GUI.
Winning takes you to the WinScreen, and losing takes you to the LoseScreen.

e) PauseScreen
Resume option to continue the game.
The Quit Game option is used to exit the current game and go back to HomeScreen.
Quit and Save option to save the game and go back to HomeScreen. This option has not been implemented yet, but it is supposed to take the user to the level at which he had exited the game.

3) Sprites
The following sprites have been created using SpriteBatch. All classes extend Sprite.

a) Bird
b) Pig
c) Slingshot
d) Glass
e) Wood
f) Win
g) Lose


GAME FLOW:

The game starts at the Home Screen.
Player can:

Start the game by clicking "PLAY."
Access settings via "SETTINGS."
Exit the game via "QUIT."

After clicking "PLAY":

The player selects a level from the Level Screen
Can return to Home Screen via "Back" button

During gameplay:

Can pause the game by accessing the Pause Screen
Win condition leads to Win Screen
Lose condition leads to Lose Screen

Music can be toggled at any point using the "VOLUME" label on the settings screen.


CONTRIBUTIONS:
Sravya Uppalapati (2023534):
HomeScreen, PauseScreen, LevelScreen, SettingsScreen, WinScreen, ReadMe file
Varsha Ganesh (2023583):
PlayScreen, LevelOne, T&CScreen, Sprites, LoseScreen, UML Diagram


ONLINE SOURCES:

We have referred to the following tutorial on YouTube.
YouTube Channel: Brent Aureli Codes
Tutorial Series: "LibGDX Mario Bros"
Link: https://youtu.be/a8MPxzkwBwo?si=eCINW42j_iafBSn6

Background pictures from Google. 

ANGRY BIRD MP3 FROM https://www.google.com/url?sa=t&source=web&rct=j&opi=89978449&url=https://www.zedge.net/find/ringtones/angry%2520birds%2520theme&ved=2ahUKEwjOx7PEmqqJAxVei2MGHYKFOFAQFnoECCkQAQ&usg=AOvVaw3CvgNX2Th6DSO4a2cRG6I3
