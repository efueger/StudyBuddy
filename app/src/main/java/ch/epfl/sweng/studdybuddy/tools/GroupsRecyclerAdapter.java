package ch.epfl.sweng.studdybuddy.tools;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.sweng.studdybuddy.Fragments.FeedFragment;
import ch.epfl.sweng.studdybuddy.R;
import ch.epfl.sweng.studdybuddy.activities.GroupActivity;
import ch.epfl.sweng.studdybuddy.activities.GroupInfoActivity;
import ch.epfl.sweng.studdybuddy.core.Group;
import ch.epfl.sweng.studdybuddy.core.Pair;
import ch.epfl.sweng.studdybuddy.services.calendar.Availability;
import ch.epfl.sweng.studdybuddy.services.calendar.ConnectedAvailability;
import ch.epfl.sweng.studdybuddy.util.FeedFilter;
import ch.epfl.sweng.studdybuddy.util.Helper;
import ch.epfl.sweng.studdybuddy.util.Messages;

public class GroupsRecyclerAdapter extends BasicRecyclerAdapter implements Filterable
{
   /* private List<Group> groupList, filterList;
    FeedFilter filter;
    private MetaGroup mb;
    private ReferenceWrapper fb;
    private String userId;
    private List<Group> uGroups;
    private HashMap<String, Integer> sizes;
    private List<String> uGroupIds;
    public Consumer<Intent> joinConsumer;
*/


    public GroupsRecyclerAdapter(List<Group> groupList, String userId)
    {
        /*this.groupList = groupList;
        this.filterList=groupList;
        mb = new MetaGroup();
        fb = new FirebaseReference();
        this.userId = userId;
        this.uGroups = new ArrayList<>();
        this.sizes = new HashMap<>();
        this.uGroupIds = new ArrayList<>();
        mb.addListenner(new RecyclerAdapterAdapter(this));
        mb.getUserGroups(userId, uGroupIds, uGroups);
        mb.getAllGroupSizes(sizes);*/
        super(groupList,userId);
    }

    public GroupsRecyclerAdapter(List<Group> groupList, String userId, Consumer<Intent> joinConsumer)
    {
        this(groupList, userId);
        this.joinConsumer = joinConsumer;
    }
    public List<Group> getGroupList() {
        return new ArrayList<>(groupList);
    }

    public void setGroupList(List<Group> groupList) {
        this.groupList = groupList;
    }



    public BasicRecyclerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View groupCardView = inflater.inflate(R.layout.recycle_viewer_row, parent, false);
        BasicRecyclerAdapter.MyViewHolder vh = getViewHolder(groupCardView);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull BasicRecyclerAdapter.MyViewHolder holder, int position) {
        Group group = groupList.get(position);
        holder.groupCourseTextView.setText(group.getCourse().getCourseName());
        holder.groupLanguageTextView.setText(group.getLang());
        holder.groupCreationDateTextView.setText(getCreationDate(group));
        //Button button = holder.messageButton;
        setParticipantNumber(holder.groupParticipantInfoTextView, group);
        setButton(holder.messageButton, group);
        if(userId.equals(group.getAdminID())) {
            holder.admin.setText("\uD83D\uDC51");
        }
        else {
            holder.admin.setText("");
        }
    }



    private String getCreationDate(Group group){
        Integer day =  group.getCreationDate().getDay();
        String string_day = day.toString();
        Integer month =  group.getCreationDate().getMonth();
        String string_month = month.toString();
        Integer year = group.getCreationDate().getYear();
        String string_year = year.toString();

        if (day < 10){
            string_day = "0" + day.toString();
        }

        if (month < 10){
            string_month = "0" + month.toString();
        }
        return string_day + "-" + string_month + "-" + string_year;
    }



    @Override
    public int getItemCount()
    {
        return groupList.size();
    }
    @Override
    public Filter getFilter() {
        if(filter==null)
        {
            filter=new FeedFilter(this,filterList);
        }

        return filter;
    }

    public void setFilterList(List<Group> newFilter)
    {
        getFilter();
        filter.setFilterList(newFilter);

    }
    private void setButton(Button button, Group group){
        Integer gSize = sizes.get(group.getGroupID().toString());
        int groupSize = 1;
        if(gSize != null){
            groupSize = gSize.intValue();
        }
        button.setText("Join");
        //Pair pair =new Pair(userId, group.getGroupID().toString());
        if(groupSize < group.getMaxNoUsers()
                &&!uGroupIds.contains(group.getGroupID().getId())) {
            button.setText("Join");
            button.setOnClickListener(joinButtonListener(group, button));
        }else {
            button.setText("More Info");
            getTheRightMoreInfo(button, group, uGroupIds.contains(group.getGroupID().getId()));

        }
    }

    private void getTheRightMoreInfo(Button button, Group group, boolean contains) {
        if (contains) {
            button.setOnClickListener(moreInfoListenerIfInTheGroup(button, group));
        } else {
            button.setOnClickListener(moreInfoListener(button, group.getGroupID().getId()));
        }
    }

    private View.OnClickListener joinButtonListener(Group group, Button button){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Pair pair =new Pair(userId, group.getGroupID().toString());
                fb.select("userGroup").select(Helper.hashCode(pair)).setVal(pair);
                Availability a = new ConnectedAvailability(pair.getKey(), pair.getValue());
                if(joinConsumer != null)
                {
                    Intent intent = new Intent(button.getContext(), GroupActivity.class);
                    intent.putExtra(FeedFragment.GROUP_ID, group.getGroupID().getId());
                    intent.putExtra(Messages.userID, userId);
                    intent.putExtra(Messages.maxUser, group.getMaxNoUsers());
                    joinConsumer.accept(intent);
                }
            }
        };
    }

    private View.OnClickListener moreInfoListener(Button button, String gId){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(joinConsumer != null )
                {
                    Intent intent = new Intent(button.getContext(), GroupInfoActivity.class);
                    intent.putExtra(FeedFragment.GROUP_ID, gId);
                    joinConsumer.accept(intent);
                }
            }
        };
    }

    private View.OnClickListener moreInfoListenerIfInTheGroup(Button button, Group group){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(joinConsumer != null )
                {
                    Intent intent = new Intent(button.getContext(), GroupActivity.class);
                    intent.putExtra(FeedFragment.GROUP_ID, group.getGroupID().getId());
                    intent.putExtra(Messages.maxUser, group.getMaxNoUsers());
                    intent.putExtra(Messages.userID, userId);
                    joinConsumer.accept(intent);
                }
            }
        };
    }


}
