# MeetingMute
Automatically mute/priority mode/vibrate your Android device based on calendar events

This is based upon RemiNIV/CalendarMute.  

## FAQ:
#### This does not work on Lollipop !
It does work on my Nexus 4 under Android 5.1.1. However, note that strictly speaking 
there is no "silent mode" in Lollipop: it has been replaced by the "Priority mode", 
and the "No interruption mode". This app will switch your device to "Priority mode" on Lollipop
if you use the "Switch to silent" option. You can then configure your "Priority mode" to disable
all sounds if you want (see your phone settings).

In "No interruption" mode, even alarms will be muted, which might not be the desired behavior (configuring
your priority mode is probably a better option).

#### What is the "Only busy events" option ?
This was requested by a user a while ago: when creating an event in Google Calendar, 
you can set your availability to "busy" or "available", among others. If you usethis option, only "busy"
events will be used. However depending on your version of the calendar app, your device, your Android version,
this feature may work or not. The good news is: this is free software, you can fix it yourself !

#### What can I legally do with this app ?
This application is released under the GNU GPL v2, do make sure you abide by the license terms when using it.
Read the license terms for more details, but to make it very (too) simple: you can do everything 
you want with the application, as long as you provide your source code with any version you release, and 
release it under the same license.
