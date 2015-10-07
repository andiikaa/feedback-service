package de.tud.feedback.plugin.openhab;

import de.tud.feedback.api.ContextUpdater;
import de.tud.feedback.plugin.openhab.domain.OpenHabItem;

import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;

public class ItemUpdateHandler {

    private final Map<String, String> cache = newHashMap();

    private final Double numberStateChangeDelta;

    private ContextUpdater updater;

    public ItemUpdateHandler(Double delta) {
        this.numberStateChangeDelta = delta;
    }

    public void setUpdater(ContextUpdater updater) {
        this.updater = updater;
    }

    public void handle(OpenHabItem item) {
        switch (item.getType()) {
            case "NumberItem": handleNumberItem(item); break;

            // other items will be ignored
            case "ColorItem":
            case "ContactItem":
            case "DateTimeItem":
            case "DimmerItem":
            case "LocationItem":
            case "RollershutterItem":
            case "StringItem":
            case "SwitchItem":
            default:
                break;
        }
    }

    private void handleNumberItem(OpenHabItem item) {
        if (!cacheContains(item)) {
            cache(item);
            update(item);

        } else {
            final String cachedState = cached(item);
            final String currentState = item.getState();

            if (!cachedState.equals(currentState) && isSignificantChange(currentState, cachedState)) {
                cache(item);
                update(item);
            }
        }
    }

    private boolean isSignificantChange(String currentState, String cachedState) {
        return Math.abs(Double.valueOf(currentState) - Double.valueOf(cachedState)) > numberStateChangeDelta;
    }

    private String cached(OpenHabItem item) {
        return cache.get(item.getLink());
    }

    private void update(OpenHabItem item) {
        updater.update(item.getName(), item.getState());
    }

    private boolean cacheContains(OpenHabItem item) {
        return cache.containsKey(item.getLink());
    }

    private String cache(OpenHabItem item) {
        return cache.put(item.getLink(), item.getState());
    }

}
