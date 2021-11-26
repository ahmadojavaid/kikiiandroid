/*
package com.example.kikkiapp.Firebase.Services;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

*
 * Created by phamngocthanh on 7/24/17.



public class GroupService implements ChildEventListener, ValueEventListener {
    private DatabaseReference mLanguagePreference;
    public boolean isLoaded = false;


    public GroupService() {
        mLanguagePreference = FirebaseDatabase.getInstance().getReference("Groups");
        mLanguagePreference.keepSynced(true);
        mLanguagePreference.addChildEventListener(this);
        mLanguagePreference.addValueEventListener(this);
    }

    public void configureDatabase() {

    }

    private Query mQuery;
    private ChangeEventListener mListener;
    private List<DataSnapshot> mSnapshots = new ArrayList<>();

    public GroupService(Query ref) {
        mQuery = ref;
        mQuery.addChildEventListener(this);
        mQuery.addValueEventListener(this);
    }

    public void updateQuery(Query ref) {
        if (mQuery != null) {
            cleanup();
        }
        mQuery = ref;
        mQuery.addChildEventListener(this);
        mQuery.addValueEventListener(this);
    }

    public void cleanup() {
        mQuery.removeEventListener((ValueEventListener) this);
        mQuery.removeEventListener((ChildEventListener) this);
    }

    public int getCount() {
        return mSnapshots.size();
    }

    public DataSnapshot getItem(int index) {
        return mSnapshots.get(index);
    }

    public int getIndexOfItem(DataSnapshot snapshot) {
        return mSnapshots.indexOf(snapshot);
    }

    private int getIndexForKey(String key) {
        int index = 0;
        for (DataSnapshot snapshot : mSnapshots) {
            if (snapshot.getKey().equals(key)) {
                return index;
            } else {
                index++;
            }
        }
        throw new IllegalArgumentException("Key not found");
    }

    public boolean existedIndexForKey(String key) {
        for (DataSnapshot snapshot : mSnapshots) {
            if (snapshot.getKey().equals(key)) {
                return true;
            }
        }
        return false;
    }

    public DataSnapshot snapshotForKey(String key) {
        for (DataSnapshot snapshot : mSnapshots) {
            if (snapshot.getKey().equals(key)) {
                return snapshot;
            }
        }
        return null;
    }

    public Group getGroupById(String key) {
        if (mSnapshots != null && mSnapshots.size() == 0) {
            return null;
        }
        for (DataSnapshot snapshot : mSnapshots) {
            if (snapshot.getKey().equals(key)) {
                return snapshot.getValue(Group.class);
            }
        }
        return null;
    }

    public User getUserBySenderId(String key) {
        if (mSnapshots != null && mSnapshots.size() == 0) {
            return null;
        }
        for (DataSnapshot snapshot : mSnapshots) {
            try {
                User customer = snapshot.getValue(User.class);
                if (customer != null && customer.userId.equalsIgnoreCase(key)) {
                    return snapshot.getValue(User.class);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public User getUserByEmail(String mEmail) {
        if (mEmail == null || mEmail.length() == 0)
            return null;
        if (mSnapshots == null || mSnapshots.size() == 0)
            return null;

        for (DataSnapshot snapshot : mSnapshots) {
            try {
                User User = snapshot.getValue(User.class);
                if (User != null && User.email != null && User.email.equalsIgnoreCase(mEmail)) {
                    return snapshot.getValue(User.class);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public User getUserByPhoneNUmber(String number) {
        if (number == null || number.length() == 0)
            return null;
        if (mSnapshots == null || mSnapshots.size() == 0)
            return null;

        for (DataSnapshot snapshot : mSnapshots) {
            try {
                User customer = snapshot.getValue(User.class);
                if (customer != null && customer.phoneNumber != null && customer.phoneNumber.equalsIgnoreCase(number)) {
                    return snapshot.getValue(User.class);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public void onChildAdded(DataSnapshot snapshot, String previousChildKey) {
        if (existedIndexForKey(snapshot.getKey())) {
            return;
        }
        int index = 0;
        if (previousChildKey != null) {
            index = getIndexForKey(previousChildKey) + 1;
        }
        mSnapshots.add(index, snapshot);
        notifyChangedListeners(ChangeEventListener.EventType.ADDED, index);
    }

    @Override
    public void onChildChanged(DataSnapshot snapshot, String previousChildKey) {
        int index = getIndexForKey(snapshot.getKey());
        mSnapshots.set(index, snapshot);
        isLoaded = true;
        notifyChangedListeners(ChangeEventListener.EventType.CHANGED, index);
    }

    @Override
    public void onChildRemoved(DataSnapshot snapshot) {
        int index = getIndexForKey(snapshot.getKey());
        mSnapshots.remove(index);
        notifyChangedListeners(ChangeEventListener.EventType.REMOVED, index);
    }

    @Override
    public void onChildMoved(DataSnapshot snapshot, String previousChildKey) {
        int oldIndex = getIndexForKey(snapshot.getKey());
        mSnapshots.remove(oldIndex);
        int newIndex = previousChildKey == null ? 0 : (getIndexForKey(previousChildKey) + 1);
        mSnapshots.add(newIndex, snapshot);
        notifyChangedListeners(ChangeEventListener.EventType.MOVED, newIndex, oldIndex);
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        mListener.onDataChanged();
    }

    @Override
    public void onCancelled(DatabaseError error) {
        notifyCancelledListeners(error);
    }

    public void setOnChangedListener(ChangeEventListener listener) {
        mListener = listener;
    }

    protected void notifyChangedListeners(ChangeEventListener.EventType type, int index) {
        notifyChangedListeners(type, index, -1);
    }

    protected void notifyChangedListeners(ChangeEventListener.EventType type, int index, int oldIndex) {
        if (mListener != null) {
            mListener.onChildChanged(type, index, oldIndex);
        }
    }

    protected void notifyCancelledListeners(DatabaseError error) {
        if (mListener != null) {
            mListener.onCancelled(error);
        }
    }

    public void sendMessage(String message, String profilePic, String myPic, String senderId, String receiverId, String type, DatabaseReference.CompletionListener completionListener) {
        Message sender = new Message();
        Message receiver = new Message();

        sender.setMessage(message);
        sender.setDeviceType("Android");
        sender.setSeen("false");
        sender.setType(type);
        sender.setUserId(receiverId);
        Date date = new Date();
        long timeMilli = date.getTime();
        sender.setTime(timeMilli/1000);

        receiver.setMessage(message);
        receiver.setDeviceType("Android");
        receiver.setSeen("false");
        receiver.setType(type);
        receiver.setUserId(senderId);
        receiver.setTime(timeMilli/1000);

        Map<String, Object> postValues1 = sender.toMap();
        Map<String, Object> postValues2 = receiver.toMap();

Map<String, Object> childUpdates1 = new HashMap<>();
        Map<String, Object> childUpdates2 = new HashMap<>();


childUpdates1.put(senderId, postValues1);
        childUpdates2.put(receiverId, postValues2);


        mLanguagePreference.child(receiverId).push().setValue(postValues1, completionListener);
    }


    public void updateUserProfile(String id, String name, String number, String email, String img, DatabaseReference.CompletionListener completionListener) {
        User user = new User();
        user.setImage(img);
        user.setUserName(name);
        user.setEmail(email);
        user.setPhoneNumber(number);

        Map<String, Object> postValues = user.toMap();

//        mLanguagePreference.updateChildren(postValues, completionListener);

        Map<String, Object> childUpdates = new HashMap<>();

//        Log.e("id",SessionManager.getKeyFirebaseId());
        childUpdates.put(id, postValues);
//        childUpdates.put(AppState.currentBpackUser.getUserId(), postValues);
        mLanguagePreference.updateChildren(childUpdates, completionListener);
    }

    public void createGroup(String name, List<String> members, DatabaseReference.CompletionListener completionListener) {
        Group group=new Group();
        group.setName(name);
        group.setAdmin(AppState.currentFireUser.getUid());
        group.setDeviceType("Android");
        group.setMembersList(members);
        group.setModerators(null);
        String key=mLanguagePreference.push().getKey();
        group.setGroupId(key);
        Date date = new Date();
        long timeMilli = date.getTime();
        group.setCreatedAt(timeMilli/1000);

        Map<String, Object> postValues = group.toMap();
        Map<String, Object> childUpdates = new HashMap<>();

        childUpdates.put(key, postValues);
        mLanguagePreference.updateChildren(childUpdates, completionListener);
        Common.ID=key;
    }


    public void updateRating(float rating, int ratingCount, String userId, DatabaseReference.CompletionListener completionListener){
        User user =new User();
        user.setRating(rating);
        user.setRatingCount(ratingCount);

        Map<String, Object> postValues = user.toMap();
        Map<String, Object> childUpdates = new HashMap<>();

        mLanguagePreference.child(userId).child("rating").setValue(rating);
        mLanguagePreference.child(userId).child("ratingCount").setValue(ratingCount);
    }

 public void updateUserLocation(String userId,Double latitude,Double longitude, DatabaseReference.CompletionListener completionListener){
       Customer customer =new Customer();
       customer.setLatitude(latitude);
       customer.setLongitude(longitude);

       mLanguagePreference.child(userId).child("longitude").setValue(longitude);
       mLanguagePreference.child(userId).child("latitude").setValue(latitude);
   }


    public void deleteGroup(String groupId, DatabaseReference.CompletionListener completionListener){
        mLanguagePreference.child(groupId).setValue(null,completionListener);
    }
}
*/
