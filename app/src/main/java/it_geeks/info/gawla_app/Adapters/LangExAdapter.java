package it_geeks.info.gawla_app.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import it_geeks.info.gawla_app.General.Common;
import it_geeks.info.gawla_app.General.SharedPrefManager;
import it_geeks.info.gawla_app.MainActivity;
import it_geeks.info.gawla_app.R;

public class LangExAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> headerList;
    private HashMap<String, List<String>> listHashMap;

    public LangExAdapter(Context context, List<String> headerList, HashMap<String, List<String>> listHashMap) {
        this.context = context;
        this.headerList = headerList;
        this.listHashMap = listHashMap;
    }

    @Override
    public int getGroupCount() {
        return headerList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return listHashMap.get(headerList.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return headerList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return listHashMap.get(headerList.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String headerLabel = (String) getGroup(groupPosition);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_lang_header, null);
        }

        TextView label = convertView.findViewById(R.id.lang_header_label);
        label.setText(headerLabel);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final String childLabel = (String) getChild(groupPosition, childPosition);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_lang_child, null);
        }
         // label
        TextView label = convertView.findViewById(R.id.lang_child_label);
        label.setText(childLabel);

        // click event
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (childLabel) {
                    case "English":
                        Common.Instance(context).setLang("en");
                        SharedPrefManager.getInstance(context).setLang("en");

                        context.startActivity(new Intent(context, MainActivity.class)
                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK));
                        break;
                    case "العربية":
                        Common.Instance(context).setLang("ar");
                        SharedPrefManager.getInstance(context).setLang("ar");

                        context.startActivity(new Intent(context, MainActivity.class)
                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK));
                        break;
                    default:
                        break;
                }
            }
        });

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
