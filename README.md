# CheckersDesktopApp
This repo will serve to store and update the software of the Checkers Desktop App project.
The game data will be stored in the Mongo Documental Database.

## Update v1.23 (09/01/2023):
- Implemented sound and media player objects to define and play sounds respectively.
- Added ISEL canvas libs to the project for their sound functions.
- Added option in the game menu to enable/disable sound.
- Removed the appearance of a dialog error when the user performed an invalid play 
on the board, so the exceptions thrown regarding this exceptional case are now 
caught silently in ViewModel.
- Added a composable to control all game dialogs.
- More tests were performed in both cmd and compose ui implementations.
- Reorg packages.

## Update v1.22 (04/01/2023):
- Fixed inverted board bug which was caused by a bad auto refresh implementation in ViewModel.
- Fixed an issue when a play was made without internet would cause an uncaught exception.
- Created 3 new dialogs (which are built using the same dialog util function) to 
represent all end game states: win, lose and draw.
- Changed all image and icon assets to sharper vector images.

## Update v1.21 (31/12/2022):
- Changed connection string options for the server timeout to be more quick in order to not let 
the user wait 30 seconds (default) for a timeout and the no internet dialog to appear after.
- Removed suboptimal function to check for internet connection for every user action.
- Introduced a new error dialog, which is an internal dialog, that shows errors as messages.
- Added more images and icons to better suit this dialog style.
- Alert Dialog is now avalaible in the generic composables package.
- Introduced try/catch blocks in viewmodel in order to catch specific exceptions and show
the corresponding dialog to the user.
- The function onCellClick, in ViewUtil.kt, still has its own try/catch block, with catch 
being empty, because it will be annoying for the user to see an error dialog after every 
miss click or invalid play on a given square. If removed in the future will be catched by 
the new error dialog on ViewModel.kt

## Update v1.20 (28/12/2022):
- Changed TextFile implementation to read files from project resources only and with that
incorporate project files inside the executable.
- Updated related test modules.
- Added encryption package to access decrypt function.

## Update v1.19 (28/12/2022):
- Created another window to introduce the game and let the user choose a board dimension
before starting.
- A dialog now appears when the application does not have internet access and the user 
performs an action. 
- Created an executable for the application.
- Encrypted connection string instead of storing it in a environment variable because,
when running the executable, the application couldn't access it. With encryption the
problem is semi-solved, since the key is still in the code.

## Update v1.18 (22/12/2022):
- In the current version, a global variable (Dimension) must be initialized before 
building the board.
- Added an option to select board dimension from a set of avalaible dimensions,
before starting the application in both ui implementations (cmd and compose).
- Refactored scale factors in CellView.kt in order to present a board which is not
hardcoded in ui generic terms. Now, when the board dimension increases, the cell view 
size won't grow linearly and instead algorithmically.
- Added a database for each avalaible dimension to avoid unexpected behaviors when 
resuming a game with a board dimension different from the one selected at the start.
- Introduced window and dialog states to ViewModel.
- Added an interface for the Radio Buttons Options.
- Separated composables that only work in the App's context from generic ones.
- Added more previews to composables.

## Update v1.17 (18/12/2022):
- An inverted board is now avalaible for the player assigned with the black checkers to
increase gameplay realism. 
- Solved CellView refresh bug. This Composable was being recomposed without necessaty when 
a cell was selected and the player remained idle.
- Created ViewUtils.kt to store both CellView and BoardView utility functions.
- Created an interface and object to enable Text File access along with a set of tests to 
verify this implementation.
- Rules is now a text file instead of hardcoded strings.
- Created another packaged inside dialog's to store App's own custom composables used in 
other dialogs. Also created previews for some of them.
- Reorganized more packages.

## Update v1.16 (16/12/2022):
- Organized model package.
- Fixed another bug regarding Kings being able to capture above own color checkers in
some situations. Added more board tests to verify the new implementation.
- Introduced scale factors to Cell View to easily adjust element sizes placed within.
- Changed rules dialog alignment.

## Update v1.15 (15/12/2022):
- Added Resume game option in the menu. This option opens a dialog window for the user to
write the game id and to choose the player to be assigned with.
- Added a new image for each checker type along with more board modifiers to present a 
better user experience, including column and rows identifiers.
- Made board compose UI flexible for every board size.
- Deleted async and await from Game.kt functions, since their use was redundant.
- Added a function to Player.kt enum class to retrieve a player label (name). Could not 
override toString since it would void the parse method in BoardSerializer.kt while retrieving
the Player with valueOf.
- Fixed a bug inside the checkWin function where another function called only covered 
Pieces types and not all checkers when evaluating the other player remaining moves.

## Update v1.14 (13/12/2022):
- Fixed a bug in a Board.kt function 'getValidAdjacentSquaresToMoveTo' where some squares with
a checker were being used as a valid play end point.
- The above-mentioned bug did not create a problem in the first CMD UI implementation 
because there were checks in the 'board.play' function to ensure that a play can only 
occur to an empty square, but while trying to show targets (valid possibilities) in the 
Compose UI implementation those checks weren't being used since show targets only displays
the squares where the play can be done and not register an actual play.
- Fixed a bug in Board extension function 'checkWin' where current turn was winning a game
when blocking the opponent's last checker but that checker could still make a capture.
- Changed game storage from File to MongoDb in ViewModel.
- Updated project dependencies.
- Added App option functionalities to ViewModel: autorefresh and showtargets.
- It is now possible to play in two instances of the application, but more testing is required.
- BoardView.kt and CellView.kt are now in a different package called 'board'.
- Created BaseImages object to store App's own images (resource paths).

## Update v1.13 (13/12/2022):
- Merged CheckersDesktopApp with CheckersCMD.
- Organized and refactored code and project files.
- Added support for Async File access and Async MongoDb access.
- Added a local global variable to store the connection string in order to protect sensitive
data. The same variable is also used to run some tests.
- Created an object that implements the MongoDbAccess.
- Implemented CheckersCMD project using async.
- All project tests passed.
- All project comments were revisited.
- Updated PlantUML file.

## Update v1.12 (11/12/2022):
- Created a StatusBar at the bottom of the App to show it's current state to the user.
- A new dialog window to help the user learn all the game's concepts is now avalaible.
- More features were added to menus, like icons and key shortcuts.
- Refactored new game dialog window with a more responsive UI and new tools to limit 
the new game creation.
- Added a package (base) inside UI to group App's defined Colors and Icons, this way 
having only one place for modification if needed.
- Imported a new composable to expand a card with a title that, when clicked,
shows the hidden text below. Some features were added to this composable, like for an 
example an Icon before to the title.
- All App's Windows, including Dialogs, are now set to not let the user resize them.

## Update v1.11 (09/12/2022):
- Started CheckersDesktopApp project development.
- The project previous UI in command line (CMD) was restructured to Compose framework 
aimed for the JVM.
- First UI implementation of the board in Compose was created and several compose funlctions 
were also created and can be accessed in the UI-Compose package.
- A ViewModel was created to store all the App's logic and state.
- Since the files that belonged to the storage package, besides BoardSerializer.kt, could 
be used for other projects, were moved to another package located outside the checkers package.
- Introduction to suspend and coroutines in some functions. Revision required.
- A New game can be created using a Dialog window, but that option was commented to easily test
other App's functionalities.
- Board equals and hashcode methods were overriden to let Compose know when a board is
diferent from another, so the recomposition occurs. Tests were created to verify this 
implementation.
- A cell in the UI Board can now be selected, but it is final purpose is incomplete.
- Added images for all checkers in the game.

## Update v1.10 (09/11/2022):
- A new board print is now avalaible.
- A new tag (CheckersCMD) was created to mark the end of the CheckersCMD development.
- After more testing today, it was determined that the prior problem wasn't resolved 
since there was a circumstance under which it may fail, which was regrettably not 
initially considered. A test was written in TestBoard.kt to address this problem, 
and others were updated utilizing the newly added functions in AuxFunctions.kt. 
Now, any piece can be placed anywhere on the board, and the player turn can be selected.
- Due to the requirement to maintain track of the previous capture in order to restrict 
the search of additional captures in subsequent turns: BoardSerializer.kt was also updated.
- PlantUML was also updated to reflect this changes.

## Update v1.09 (07/11/2022):
- Minor changes to BoardRun.getAvalaibleCaptures() function.
- Project plant UML sketch was designed. Current implementation might not be final.
- Added companion objects to plant UML design. 
- Introduced fold instead of foreach in AuxFunctions.kt.
- In the plantUML design, objects in package model were replaced by singleton to 
better represent the respective companion objects of a class.
- Fixed a bug where after performing a capture with a checker, which had no 
mandatory captures left, the turn was still kept due to a mandatory capture 
associated with checker.

## Update v1.08 (06/11/2022):
- More comments were added to newly created functions and others were revisited. 
- TestChecker.kt was created to verify equals() and hashcode() new implementations.

## Update v1.07 (05/11/2022):
- Checkwin extension function was condensed in a smaller logic. 
- More properties were added to Square.kt and more tests were made to verify them. 
- Function to retrieve avalaible captures was redefined along with several auxiliary
functions. 
- More tests were added to TestBoard.kt and some were updated.

## Update v1.06 (04/11/2022):
- Finished Refresh and Continue commands.
- Changed BoardSerializer.kt player retrieval, might not be the final implementation.
- Further tests are required, also with the MongoDB.

## Update v1.05 (03/11/2022):
- Finished BoardSerializer.kt, and both MongoStorage.kt and FileStorage.kt tests.

## Update v1.04 (02/11/2022):
- Finished first implementation of Board.kt. 
- Several tests were made to verify this implementation and can be found in TestBoard.kt.

## Update v1.03 (16/10/2022):
- Finished implementation of Square.kt.
- All given associated tests have approved this implementation.

## Update v1.02 (15/10/2022):
- Finished implementation of Row.kt.
- All given associated tests have approved this implementation.

## Update v1.01 (15/10/2022):
- Started CheckersCMD project development.
- Finished implementation of Column.kt.
- All given associated tests have approved this implementation.