/**
 * Copyright (c) 2014-2015 openHAB UG (haftungsbeschraenkt) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.smarthome.core.thing.internal.console;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.link.ItemChannelLink;
import org.eclipse.smarthome.core.thing.link.ItemChannelLinkRegistry;
import org.eclipse.smarthome.io.console.Console;
import org.eclipse.smarthome.io.console.extensions.AbstractConsoleCommandExtension;

/**
 * {@link LinkConsoleCommandExtension} provides console commands for listing,
 * addding and removing links.
 *
 * @author Dennis Nobel - Initial contribution
 * @author Alex Tugarev - Added support for links between items and things
 * @author Kai Kreuzer - Removed Thing link commands
 */
public class LinkConsoleCommandExtension extends AbstractConsoleCommandExtension {

    private static final String SUBCMD_LIST = "list";
    private static final String SUBCMD_CL_ADD = "addChannelLink";
    private static final String SUBCMD_CL_REMOVE = "removeChannelLink";
    private static final String SUBCMD_CLEAR = "clear";

    private ItemChannelLinkRegistry itemChannelLinkRegistry;

    public LinkConsoleCommandExtension() {
        super("links", "Manage your links.");
    }

    @Override
    public void execute(String[] args, Console console) {
        if (args.length > 0) {
            String subCommand = args[0];
            switch (subCommand) {
                case SUBCMD_LIST:
                    list(console, itemChannelLinkRegistry.getAll());
                    return;
                case SUBCMD_CL_ADD:
                    if (args.length > 2) {
                        String itemName = args[1];
                        ChannelUID channelUID = new ChannelUID(args[2]);
                        addChannelLink(console, itemName, channelUID);
                    } else {
                        console.println("Specify item name and channel UID to link: link <itemName> <channelUID>");
                    }
                    return;
                case SUBCMD_CL_REMOVE:
                    if (args.length > 2) {
                        String itemName = args[1];
                        ChannelUID channelUID = new ChannelUID(args[2]);
                        removeChannelLink(console, itemName, channelUID);
                    } else {
                        console.println("Specify item name and channel UID to unlink: link <itemName> <channelUID>");
                    }
                    return;
                case SUBCMD_CLEAR:
                    clear(console);
                    return;
                default:
                    break;
            }
        } else {
            list(console, itemChannelLinkRegistry.getAll());
        }
    }

    @Override
    public List<String> getUsages() {
        return Arrays
                .asList(new String[] { buildCommandUsage(SUBCMD_LIST, "lists all links"),
                        buildCommandUsage(SUBCMD_CL_ADD + " <itemName> <channelUID>", "links an item with a channel"),
                        buildCommandUsage(SUBCMD_CL_REMOVE + " <itemName> <thingUID>",
                                "unlinks an item with a channel"),
                buildCommandUsage(SUBCMD_CLEAR, "removes all managed links") });
    }

    private void clear(Console console) {
        Collection<ItemChannelLink> itemChannelLinks = itemChannelLinkRegistry.getAll();
        for (ItemChannelLink itemChannelLink : itemChannelLinks) {
            itemChannelLinkRegistry.remove(itemChannelLink.getID());
        }
        console.println(itemChannelLinks.size() + " links successfully removed.");
    }

    private void addChannelLink(Console console, String itemName, ChannelUID channelUID) {
        ItemChannelLink itemChannelLink = new ItemChannelLink(itemName, channelUID);
        itemChannelLinkRegistry.add(itemChannelLink);
        console.println("Link " + itemChannelLink.toString() + " successfully added.");
    }

    private void list(Console console, Collection<ItemChannelLink> itemChannelLinks) {
        for (ItemChannelLink itemChannelLink : itemChannelLinks) {
            console.println(itemChannelLink.toString());
        }
    }

    private void removeChannelLink(Console console, String itemName, ChannelUID channelUID) {
        ItemChannelLink itemChannelLink = new ItemChannelLink(itemName, channelUID);
        ItemChannelLink removedItemChannelLink = itemChannelLinkRegistry.remove(itemChannelLink.getID());
        if (removedItemChannelLink != null) {
            console.println("Link " + itemChannelLink.toString() + "successfully removed.");
        } else {
            console.println("Could not remove link " + itemChannelLink.toString() + ".");
        }
    }

    protected void setItemChannelLinkRegistry(ItemChannelLinkRegistry itemChannelLinkRegistry) {
        this.itemChannelLinkRegistry = itemChannelLinkRegistry;
    }

    protected void unsetItemChannelLinkRegistry(ItemChannelLinkRegistry itemChannelLinkRegistry) {
        this.itemChannelLinkRegistry = null;
    }

}
