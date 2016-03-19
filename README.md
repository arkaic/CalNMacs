# My Personal Calorie Tracker Android App
[Currently in progress]

The aim for this app is a learning exercise in Android, as it's my first time.

Also, I wanted calorie tracking to be as simple as possible. I didn't like the bloated, feature-rich apps that's in the store, so I wanted to make an app that just tracks your calories, the macros, and included an editable database. 

The food database will not be [necessarily] based on any public databases out there, but a personal one that the user should be able to add to, delete from, and edit. If you're like me, on a day to day basis, I eat generally the same types of foods, so having a database of commonly eaten foods instead of an all encompassing list is much more manageable.

Life is complex and I prefer not to add too much more to it, so simply keeping track of the calorie count and the three macros is sufficient for me.


## Features and Layout
The app is planned to have two pages that should each swipe laterally to the other.

The left page is the main page, where you can add/delete/edit foods you have eaten for the day. This should be saved between app suspensions/pauses/closures until you hit the CLEAR ALL button. Foods will only have carb, fat, protein, and calorie counts. When choosing foods, you just need to choose the amount you have eaten. (I plan on adding the ability to switch unit types for any given food, but for now, each food item in the sqlite database will have their own 'unit type' column)

The right page is the food list that displays all foods in the database. I will prepopulate the database with my own foods for now, but in the future, I may have the app start off with an empty database and allow the user the ability to populate it with a csv file.