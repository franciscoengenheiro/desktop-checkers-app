# CheckersDesktopApp
This repo will serve to store and update the software of the Checkers Desktop App project.
The game data will be stored in the Mongo Documental Database.

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
- First UI implementation of the board in Compose was created and several compose functions 
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
- 
## Update v1.02 (15/10/2022):
- Finished implementation of Row.kt.
- All given associated tests have approved this implementation.

## Update v1.01 (15/10/2022):
- Started CheckersCMD project development.
- Finished implementation of Column.kt.
- All given associated tests have approved this implementation.