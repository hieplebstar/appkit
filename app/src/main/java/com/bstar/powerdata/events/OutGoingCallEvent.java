package com.bstar.powerdata.events;

import com.bstar.powerdata.models.Contact;

import lombok.Data;

/**
 * Created by HiepLe on 11/14/2016.
 */
@Data
public class OutGoingCallEvent {
    final Contact contact;
}
