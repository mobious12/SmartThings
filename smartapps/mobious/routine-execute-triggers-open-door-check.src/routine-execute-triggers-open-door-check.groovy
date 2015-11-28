/**
 *  Routine Triggers Open Door Check
 *
 *  Copyright 2015 Glen McGowan
 *
 */
definition(
    name: "Routine Execute Triggers Open Door Check",
    namespace: "mobious",
    author: "Glen McGowan",
    description: "Check for opent doors when routine executes",
    category: "Convenience",
    iconUrl: "http://cdn.device-icons.smartthings.com/Home/home2-icn.png",
    iconX2Url: "http://cdn.device-icons.smartthings.com/Home/home2-icn@2x.png"
)


preferences{
    page(name: "selectActions")
}

def selectActions() {
    dynamicPage(name: "selectActions", install: true, uninstall: true) {

        // Get the available routines
            def actions = location.helloHome?.getPhrases()*.label
            if (actions) {
            // sort them alphabetically
            actions.sort()
                    section("Routine") {
                        log.trace actions
                		// use the actions as the options for an enum input
                		input "action", "enum", title: "Select a trigger routine", options: actions
                    	}
                    section("Doors to check"){
						input "doors", "capability.contactSensor", title: "Which Doors?", multiple: true, required: true
    					}
                    /**section("Notification"){
        				input "sendPush", "bool",title: "Send Push Notification?", required: true
        				input "phone", "phone", title: "Phone Number (for SMS, optional)", required: false
        				input "pushAndPhone", "enum", title: "Both Push and SMS?", required: false, options: ["Yes", "No"]
   						}**/    
                    
            }
    }
}
def installed() {
	log.debug "Installed with settings: ${settings}"
	initialize()
}

def updated() {
	log.debug "Updated with settings: ${settings}"
	unsubscribe()
	initialize()
}

def initialize() {
        subscribe(location, "routineExecuted", routineChanged)
    }


def routineChanged(evt) {
 	if (evt.displayName == action)
        checkDoor();
}


def checkDoor() {
	log.debug "checkDoor status: ${doors.displayName} is ${doors.currentContact}"
    if (doors.currentContact == "closed") {
		def message = "Door was left open"
    	log.debug message
        if (sendPush) {
     		sendPush(message)
     		}
      }
}


/**Fancy Notification--Need to Fix later
def checkDoor() {
    log.debug "checkDoor status: ${door.displayName} is ${door.currentContact}"
   	if (door.currentContact == "open") {
       	log.debug "checkDoor Debug: ${door.currentContact}"
        def msg = "${door.displayName} was left open!"
        log.debug msg
        
         if (!phone || pushAndPhone != "No") {
             log.debug "checkDoor: Sending push notificaton"
             sendPush(msg)
         }
         if (phone) {
            log.debug "checkDoor: Sending SMS Notification"
            sendSms(phone, msg)
        
         } else {
       	 log.debug "checkDoor Complete: No Open Doors Detected"
         } 
	} else {
    log.debug "checkDoor Error: Unable to detect contact states"
}
}
**/