/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.bluetoothchat;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.common.logger.Log;

import java.security.AccessController;
import java.util.HashMap;

/**
 * This fragment controls Bluetooth to communicate with other devices.
 */
public class BluetoothChatFragment extends Fragment {
    public int turn_player=0;
    DataBaseHelper helper;
    public int temps=0;
    public int turn_playing=0;
    private static BluetoothChatService mChatService = null;
    public static final String TAG = "BluetoothChatFragment";
    // views
    public ImageButton button0,button1,button2,button3,button4,button5,button6,button7,button8;
    public static HashMap<Integer, ImageButton> buttonMap;

    public static  OnGameInteractionListener mListener = null;
    // TODO change the first player
    public static final int me = 1;
    public  String score,username;
    public TextView score_txt;
    public static int currentPlayer;
    public static Game game;
    public BluetoothChatFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
    public void startNewGame(int firstGamer) {
        if(mChatService.getState()==3)
        {
            game = new Game(); temps=0;
            //if(turn_player==1)
            sendMessage("I've started(restarted) the game, click on start button to play the game!");
            currentPlayer = firstGamer; // should be 1 or 2
            for(ImageButton button : buttonMap.values()) {
                button.setImageResource(R.drawable.boost);
                button.setTag(R.drawable.boost);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    button.setBackground(null);
                }
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bTapped(v);
                    }
                });
               // Toast.makeText(getActivity(), Integer.toString((Integer) button.getTag()), Toast.LENGTH_SHORT).show();
            }
        }
        else
            Toast.makeText(getActivity(),"PLEASE CONNECT FIRST TO A DEVICE AND THEN START THE GAME",Toast.LENGTH_SHORT).show();
    }


    private void bTapped(View v) {
        //temps++;
        if(turn_playing==1){
            switch(v.getId()) {
                case R.id.button0:  play(0); break;
                case R.id.button1:  play(1); break;
                case R.id.button2:  play(2); break;
                case R.id.button3:  play(3); break;
                case R.id.button4:  play(4); break;
                case R.id.button5:  play(5); break;
                case R.id.button6:  play(6); break;
                case R.id.button7:  play(7); break;
                case R.id.button8:  play(8); break;
                default:
                    Log.d("Debug#Control", "wrong button pressed");
                    break;
            }
        }
    }

    private void play(int location) {
        boolean lost=false; temps++;
        if(turn_playing==1)
            turn_playing=0;
        else
            turn_playing=1;
        if(location>=10)
        {
            lost=true;
            location-=10;
        }
            boolean cellMarked = game.markACell(currentPlayer, location);
            boolean gameWon = game.checkForWin();
            if(cellMarked) {
                // game finished
                if(gameWon) {
                    Toast.makeText(this.getActivity(), "Finish game! Press Start to reset the game!", Toast.LENGTH_LONG).show();
                    sendMessage("BlueGameTicTocToe" + (Integer.toString(location+10)));
                    updateView(currentPlayer, location);
                    rmvButtonLisntener();
                    // display dialog
                    Dialog dialog=new Dialog(getActivity());
                    dialog.setContentView(R.layout.dialog_game_won);
                    ImageView img = (ImageView) dialog.findViewById(R.id.imageView);
                    //update score

                    if(lost==true){
                        img.setImageResource(R.drawable.loser2);
                    }
                    else{
                        score_txt=(TextView)getActivity().findViewById(R.id.score_txt);
                        String value=score_txt.getText().toString();
                        String [] str =value.split(",");
                        username= str[0];
                        str[1]=str[1].replace("your score: ","");
                        score=str[1];
                        int intScore=Integer.parseInt(score);
                        intScore++;String s=Integer.toString(intScore);
                        helper.updateScore(username, s);
                        score_txt.setText(username+ ",your score: "+Integer.toString(intScore));
                    }
                    dialog.show();
                    //
                    mListener.onGameWon(currentPlayer);
                }
                // current turn finished
                else{
                    if(temps==21 ){
                        Toast.makeText(this.getActivity(), "Equal condition, restart the game by clicking on Start", Toast.LENGTH_LONG).show();
                        Dialog dialog=new Dialog(getActivity());
                        dialog.setContentView(R.layout.dialog_game_won);
                        ImageView img = (ImageView) dialog.findViewById(R.id.imageView);
                        img.setImageResource(R.drawable.equal);
                        dialog.show();
                    }
                    //Toast.makeText(this.getActivity(), Integer.toString (temps), Toast.LENGTH_SHORT).show();
                    sendMessage("BlueGameTicTocToe"+Integer.toString(location));
                    updateView(currentPlayer, location);
                    // TODO Wait for other user and delete the following lines
                    switch (currentPlayer){
                        case 1:
                            currentPlayer = 2; break;
                        case 2:
                            currentPlayer = 1; break;
                    }
                }
            }


    }
    public void setCurrentPlayer(int currentPlayer) {
        this.currentPlayer = currentPlayer;
    }
    public static void rmvButtonLisntener() {
        for(ImageButton button : buttonMap.values()) {
            button.setOnClickListener(null);
        }
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnGameInteractionListener) activity;
            //fragmentOneListener = (BluetoothChatFragmentListener) activity;
        } catch (ClassCastException e) {
            // Unchecked exception.
            throw new ClassCastException(activity.toString()
                    + " must implement OnGameInteractionListener");
        }
    }
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnGameInteractionListener {
        void onGameWon(int selectedCase);
    }

    private void  updateView(int player, int location) {

        /**
         * First player : O
         * Second player : X
         */

        if(location < buttonMap.size() ) {
            switch (player) {
                case 1:
                    (buttonMap.get(location)).setImageResource(R.drawable.circle);
                    (buttonMap.get(location)).setTag(R.drawable.circle);
                    break;
                case 2:
                    (buttonMap.get(location)).setImageResource(R.drawable.croix);
                    (buttonMap.get(location)).setTag(R.drawable.croix);
                    break;
            }
        }
        else {
            Log.d("Debug#44", "could not update the view #" + location);
        }

    }


    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;

    // Layout Views
    private ListView mConversationView;
    private static EditText mOutEditText;
    private Button mSendButton;

    /**
     * Name of the connected device
     */
    private String mConnectedDeviceName = null;

    /**
     * Array adapter for the conversation thread
     */
    private ArrayAdapter<String> mConversationArrayAdapter;

    /**
     * String buffer for outgoing messages
     */
    private static StringBuffer mOutStringBuffer;

    /**
     * Local Bluetooth adapter
     */
    private BluetoothAdapter mBluetoothAdapter = null;

    /**
     * Member object for the chat services
     */




    @Override
    public void onStop(){
        Log.w(TAG, "App stopped");
        mBluetoothAdapter.disable();
        super.onStop();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        turn_player=0; turn_playing=0;
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            FragmentActivity activity = getActivity();
            Toast.makeText(activity, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            activity.finish();
        }

        helper=new DataBaseHelper(this.getContext());
    }


    @Override
    public void onStart() {
        super.onStart();
        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the chat session
        } else if (mChatService == null) {
            setupChat();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mChatService != null) {
            mChatService.stop();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (mChatService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
                // Start the Bluetooth chat services
                mChatService.start();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_bluetooth_chat, container, false);
        button0 = (ImageButton) view.findViewById(R.id.button0);
        button0.setTag(R.drawable.circle);
        button1 = (ImageButton) view.findViewById(R.id.button1);
        button1.setTag(R.drawable.croix);
        button2 = (ImageButton) view.findViewById(R.id.button2);
        button2.setTag(R.drawable.circle);
        button3 = (ImageButton) view.findViewById(R.id.button3);
        button3.setTag(R.drawable.croix);
        button4 = (ImageButton) view.findViewById(R.id.button4);
        button4.setTag(R.drawable.circle);
        button5 = (ImageButton) view.findViewById(R.id.button5);
        button5.setTag(R.drawable.croix);
        button6 = (ImageButton) view.findViewById(R.id.button6);
        button6.setTag(R.drawable.circle);
        button7 = (ImageButton) view.findViewById(R.id.button7);
        button7.setTag(R.drawable.croix);
        button8 = (ImageButton) view.findViewById(R.id.button8);
        button8.setTag(R.drawable.circle);

        buttonMap = new HashMap<>();
        buttonMap.put(0,button0);
        buttonMap.put(1,button1);
        buttonMap.put(2,button2);
        buttonMap.put(3,button3);
        buttonMap.put(4,button4);
        buttonMap.put(5,button5);
        buttonMap.put(6,button6);
        buttonMap.put(7, button7);
        buttonMap.put(8, button8);


        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mConversationView = (ListView) view.findViewById(R.id.in);
        mOutEditText = (EditText) view.findViewById(R.id.edit_text_out);
        mSendButton = (Button) view.findViewById(R.id.button_send);
        //score.replace("Your score: ","");

    }

    /**
     * Set up the UI and background operations for chat.
     */
    private void setupChat() {
        Log.d(TAG, "setupChat()");

        // Initialize the array adapter for the conversation thread
        mConversationArrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.message);

        mConversationView.setAdapter(mConversationArrayAdapter);

        // Initialize the compose field with a listener for the return key
        mOutEditText.setOnEditorActionListener(mWriteListener);

        // Initialize the send button with a listener that for click events
        mSendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Send a message using content of the edit text widget
                View view = getView();
                if (null != view) {
                    TextView textView = (TextView) view.findViewById(R.id.edit_text_out);
                    String message = textView.getText().toString();
                    sendMessage(message);
                }
            }
        });

        // Initialize the BluetoothChatService to perform bluetooth connections
        mChatService = new BluetoothChatService(getActivity(), mHandler);

        // Initialize the buffer for outgoing messages
        mOutStringBuffer = new StringBuffer("");
    }

    /**
     * Makes this device discoverable.
     */
    private void ensureDiscoverable() {
        if (mBluetoothAdapter.getScanMode() !=
                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }

    /**
     * Sends a message.
     *
     * @param message A string of text to send.
     */
    private  void sendMessage(String message) {
        // Check that we're actually connected before trying anything
        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
            //Toast.makeText(getActivity(), R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            mChatService.write(send);

            // Reset out string buffer to zero and clear the edit text field
            mOutStringBuffer.setLength(0);
            mOutEditText.setText(mOutStringBuffer);
        }
    }

    /**
     * The action listener for the EditText widget, to listen for the return key
     */
    private TextView.OnEditorActionListener mWriteListener
            = new TextView.OnEditorActionListener() {
        public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
            // If the action is a key-up event on the return key, send the message
            if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_UP) {
                String message = view.getText().toString();
                sendMessage(message);
            }
            return true;
        }
    };

    /**
     * Updates the status on the action bar.
     *
     * @param resId a string resource ID
     */
    private void setStatus(int resId) {
        FragmentActivity activity = getActivity();
        if (null == activity) {
            return;
        }
        final ActionBar actionBar = activity.getActionBar();
        if (null == actionBar) {
            return;
        }
        actionBar.setSubtitle(resId);
    }

    /**
     * Updates the status on the action bar.
     *
     * @param subTitle status
     */
    private void setStatus(CharSequence subTitle) {
        FragmentActivity activity = getActivity();
        if (null == activity) {
            return;
        }
        final ActionBar actionBar = activity.getActionBar();
        if (null == actionBar) {
            return;
        }
        actionBar.setSubtitle(subTitle);
    }

    /**
     * The Handler that gets information back from the BluetoothChatService
     */
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            FragmentActivity activity = getActivity();
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothChatService.STATE_CONNECTED:
                            if(isAdded()){
                                setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));
                                mConversationArrayAdapter.clear();
                            }
                            break;
                        case BluetoothChatService.STATE_CONNECTING:
                            turn_player=1; turn_playing=1;
                            setStatus(R.string.title_connecting);
                            break;
                        case BluetoothChatService.STATE_LISTEN:
                        case BluetoothChatService.STATE_NONE:
                            setStatus(R.string.title_not_connected);
                            break;
                    }
                    break;
                case Constants.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    if(writeMessage.contains("BlueGameTicTocToe")){
                        writeMessage=writeMessage.replace("BlueGameTicTocToe","");
                        int loc=Integer.parseInt(writeMessage);
                        play(loc);
                    }
                    else if(writeMessage.contains("I've started(restarted) the game, click on start button to play the game!"))
                        break;//be oonyeki bege turne toe
                    else
                        mConversationArrayAdapter.add("Me:  " + writeMessage);
                    break;
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj; boolean needStart=false;
                    for(ImageButton button : buttonMap.values()) {
                        if((Integer)button.getTag()!=2130837575)
                            needStart=true;
                    }
                    int tagesh=(Integer) button1.getTag();
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    if(readMessage.contains("BlueGameTicTocToe")){
                        turn_playing=1;
                        readMessage=readMessage.replace("BlueGameTicTocToe","");
                        int loc=Integer.parseInt(readMessage);
                        play(loc);
                    }
                    else if(readMessage.contains("I've started(restarted) the game, click on start button to play the game!") && !needStart)
                        break;//be oonyeki bege turne toe
                    else
                        mConversationArrayAdapter.add(mConnectedDeviceName + ":  " + readMessage);
                    break;
                case Constants.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
                    if (null != activity) {
                        Toast.makeText(activity, "Connected to "
                                + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    }
                    break;
                case Constants.MESSAGE_TOAST:
                    if (null != activity) {
                        Toast.makeText(activity, msg.getData().getString(Constants.TOAST),
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE_SECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, true);
                }
                break;
            case REQUEST_CONNECT_DEVICE_INSECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, false);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    setupChat();
                } else {
                    // User did not enable Bluetooth or an error occurred
                    Log.d(TAG, "BT not enabled");
                    Toast.makeText(getActivity(), R.string.bt_not_enabled_leaving,
                            Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                }
        }
    }

    /**
     * Establish connection with other divice
     *
     * @param data   An {@link Intent} with {@link DeviceListActivity#EXTRA_DEVICE_ADDRESS} extra.
     * @param secure Socket Security type - Secure (true) , Insecure (false)
     */
    private void connectDevice(Intent data, boolean secure) {
        // Get the device MAC address
        String address = data.getExtras()
                .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        mChatService.connect(device, secure);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.bluetooth_chat, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.secure_connect_scan: {
                // Launch the DeviceListActivity to see devices and do scan
                Intent serverIntent = new Intent(getActivity(), DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
                return true;
            }
            case R.id.insecure_connect_scan: {
                // Launch the DeviceListActivity to see devices and do scan
                Intent serverIntent = new Intent(getActivity(), DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_INSECURE);
                return true;
            }
            case R.id.discoverable: {
                // Ensure this device is discoverable by others
                ensureDiscoverable();
                return true;
            }
        }
        return false;
    }

}
