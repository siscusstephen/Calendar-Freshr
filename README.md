Calendar Freshr Challenge
=========================

**The dependecies that i used in this project**

dependencies {
    compile project(':caldroid')
    compile 'com.android.support:support-v4:23.0.1'
    compile 'com.android.support:appcompat-v7:23.0.1'
}

So i decided to use caldroid as the calendar view for my program.

I fulfilled all the requirements.

I used FILE for saving all the event datas for now.

DataProcess Class was created for data processing (Save,Edit,Delete,Get,WriteToFile0

MainActivity is the main activity that hold the calendar view

AddEventActivity is the activity when user want to add an event.

EditEventActivity is the activity when user want to edit or delete an event.

Features
========

-. User can create multiple events in a day with different time for each event.

-. An event is consisted of a start time and a finish time.

-. User can edit/delete an event.

-. User cannot create/edit event in the past (yesterday or older).

-. User cannot create event which will overlap another event in time.

-. System always validate the input.

-. When the user save an event which will overlap some other events, show a popup dialog to warn the user what time periods have conflicts.

-. Events in the application should persist even if the app is closed or the phone is restarted.
