/**
 *  Routine Triggers Open Door Check
 *
 *  Copyright 2015 Glen McGowan
 *
 */
definition(
    name: "Routine Triggers Open Door Check",
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
						input "Door", "capability.contactSensor", title: "Which Doors?", multiple: true, required: true
    					}
                    section("Notification"){
        				input "sendPush", "bool",title: "Send Push Notification?", required: true
        				input "phone", "phone", title: "Phone Number (for SMS, optional)", required: false
        				input "pushAndPhone", "enum", title: "Both Push and SMS?", required: false, options: ["Yes", "No"]
   						}    
                    
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
        subscribe(location, Door, "routineExecuted", routineChanged, checkDoor, doorCheckHandler)
    }


def routineChanged(evt) {
    // name will be "routineExecuted"
    log.debug "evt name: ${evt.name}"

    // value will be the ID of the SmartApp that created this event
    log.debug "evt value: ${evt.value}"

    // displayName will be the name of the routine
    // e.g., "I'm Back!" or "Goodbye!"
    log.debug "evt displayName: ${evt.displayName}"

    // descriptionText will be the name of the routine, followed by the action
    // e.g., "I'm Back! was executed" or "Goodbye! was executed"
    log.debug "evt descriptionText: ${evt.descriptionText}"
}

def doorCheckHandler(evt) {
 	log.debug "Detected Routine Complete, Executing Door Check"
    if (evt.displayName == action) {
    checkDoor()        
}
}


def checkDoor() {
    log.debug "Door ${door.displayName} is ${door.currentContact}"
   	if (door.currentContact == "open") {
       	def msg = "${door.displayName} was left open!"
        log.info msg
        
         if (!phone || pushAndPhone != "No") {
             log.debug "sending push"
             sendPush(msg)
         }
         if (phone) {
            log.debug "sending SMS"
            sendSms(phone, msg)
        
         } else {
       	 log.debug "Door Check Complete: No Open Doors"
    }
}
}