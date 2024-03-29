package app.insti.adapter;

import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import java.util.List;

import app.insti.R;
import app.insti.Utils;
import app.insti.api.model.Event;

public class FeedAdapter extends CardAdapter<Event> {
    public FeedAdapter(List<Event> eventList, Fragment fragment) {
        super(eventList, fragment);
    }

    @Override
    public void onClick(Event event, FragmentActivity fragmentActivity) {}

    @Override
    public void onClick(Event event, final Fragment fragment, View view) {
        int picId = R.id.object_picture;
        if (event.isEventBigImage()) {
            picId = R.id.big_object_picture;
        }
        Utils.openEventFragment(event, fragment, view.findViewById(picId));
    }

    @Override
    public String getBigImageUrl(Event event) {
        if (event.isEventBigImage()) {
            return event.getEventImageURL();
        }
        return null;
    }

    @Override
    public int getAvatarPlaceholder(Event event) {
        return Utils.isDarkTheme ? R.drawable.lotus_placeholder_dark : R.drawable.lotus_placeholder;
    }
}
