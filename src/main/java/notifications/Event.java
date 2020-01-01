/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package notifications;

/**
 *
 * @author cuong
 */
public class Event {
    public final EventType event;
    public final Status status;

    public Event(EventType event, Status status) {
        this.event = event;
        this.status = status;
    }
}
