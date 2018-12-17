# AlphaFitness

 Mobile application that helps user's keep track of statistics gathered from working out

## Screenshots
 I will add some screenshots soon!

## Additional Information
 
 The UI needs to be polished, specifically the graphs are not appealing.
 I am using a Remote Service that broadcasts the state of a workout every second. 
 I use Broadcast Receivers that run on background threads to receive the state of a 
 workout and post updates to a Handler running on the main thread that will then 
 update the UI. Since the application needs to be able to continue tracking a workout if the user 
 turns the phone screen off, I was unable to come up with an efficient solution
 to syncing the workout state between an Activity and the Remote Service. My
 solution involves the Remote Service broadcasting the entire state of a workout
 to the Broadcast Receivers and in the onReceive method I instantiate a new Workout
 object each time. This is inefficient, but it works. 
 
 Name, gender, and weight are persisted using SharedPreferences. All workout data and 
 location data are persisted using a ContentProvider and a SQLite Database.



