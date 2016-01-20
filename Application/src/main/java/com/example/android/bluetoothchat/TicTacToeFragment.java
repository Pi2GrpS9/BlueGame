package com.example.android.bluetoothchat;
import android.app.ActionBar;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.ByteBuffer;
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class TicTacToeFragment extends Fragment {

    private EditText mOutEditText;
    private StringBuffer mOutStringBuffer;
    // TODO change the first player
    private static final int firstGamer = 1;
    private static final int me = 1;
    private Game game;
    BluetoothChatService mChatService = null;
    private String mConnectedDeviceName = null;
    // views
    private ImageButton button0,button1,button2,button3,button4,button5,button6,button7,button8;
    private HashMap<Integer, ImageButton> buttonMap;
    private ArrayAdapter<String> mConversationArrayAdapter;




    public TicTacToeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tic_tac_toe, container, false);
        button0 = (ImageButton) view.findViewById(R.id.button0);
        button1 = (ImageButton) view.findViewById(R.id.button1);
        button2 = (ImageButton) view.findViewById(R.id.button2);
        button3 = (ImageButton) view.findViewById(R.id.button3);
        button4 = (ImageButton) view.findViewById(R.id.button4);
        button5 = (ImageButton) view.findViewById(R.id.button5);
        button6 = (ImageButton) view.findViewById(R.id.button6);
        button7 = (ImageButton) view.findViewById(R.id.button7);
        button8 = (ImageButton) view.findViewById(R.id.button8);

        buttonMap = new HashMap<>();
        buttonMap.put(0,button0);
        buttonMap.put(1,button1);
        buttonMap.put(2,button2);
        buttonMap.put(3,button3);
        buttonMap.put(4,button4);
        buttonMap.put(5,button5);
        buttonMap.put(6,button6);
        buttonMap.put(7,button7);
        buttonMap.put(8, button8);

        for(ImageButton button : buttonMap.values()) {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bTapped(v);
                }
            });
        }

        mOutEditText = (EditText) view.findViewById(R.id.edit_text_out);

        mOutStringBuffer = new StringBuffer("");
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mChatService = new BluetoothChatService(getActivity(), mHandler);
    }
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            FragmentActivity activity = getActivity();
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothChatService.STATE_CONNECTED:
                            setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));
                            mConversationArrayAdapter.clear();
                            break;
                        case BluetoothChatService.STATE_CONNECTING:
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
                    mConversationArrayAdapter.add("Me:  " + writeMessage);
                    break;
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
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

    //need for mhandler:
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

    @Override
    public void onStart() {
        super.onStart();

        startNewGame();
    }

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

    private void sendMessage(String message) {
        // Check that we're actually connected before trying anything
        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
            Toast.makeText(getActivity(), R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            /*********** alireza tadbir ******************/
            byte[] send = message.getBytes();
            mChatService.write(send);

            // Reset out string buffer to zero and clear the edit text field
            mOutStringBuffer.setLength(0);
            mOutEditText.setText(mOutStringBuffer);
        }
    }

    private void startNewGame() {
        game = new Game(firstGamer);

        // initializing button text
        for(ImageButton button : buttonMap.values()) {
            button.setImageResource(R.drawable.boost);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                button.setBackground(null);
            }
        }


        Snackbar.make(this.button0, "New game started, player : " + firstGamer, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();

    }

    // update the text in the button when a player mark a cell
    private void updateView(int player, int location) {

        /**
         * First player : O
         * Second player : X
         */

        if(location < buttonMap.size() ) {
            switch (player) {
                case 1:
                    (buttonMap.get(location)).setImageResource(R.drawable.circle);
                    break;
                case 2:
                    (buttonMap.get(location)).setImageResource(R.drawable.croix);
                    break;
            }
        }
        else {
            Log.d("Debug#44", "could not update the view #" + location);
        }

    }

    // catch event from the layout
    public void bTapped(View view) {
        switch(view.getId()) {
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


    //need to be called at each row
    private void play(int location) {

        mOutEditText.setOnEditorActionListener(mWriteListener);
        //mChatService = new BluetoothChatService(getActivity(), mHandler);
        /*********** alireza tadbir ******************/
        /*if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
            Toast.makeText(getActivity(), R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }*/

        // Check that there's actually something to send
        //if (location)
        {
            //StringBuffer mOutStringBuffer;

            // Get the message bytes and tell the BluetoothChatService to write
            /*********** alireza tadbir ******************/
            byte[] send = ByteBuffer.allocate(4).putInt(location).array();
            mChatService.write(send);

            // Reset out string buffer to zero and clear the edit text field
            mOutStringBuffer.setLength(0);
            mOutEditText.setText(mOutStringBuffer);
        }









       // byte[] send = ByteBuffer.allocate(4).putInt(location).array();
       // mChatService.write(send);



        boolean cellMarked = game.markACell(location);
        boolean gameWon = game.checkForWin();

        if(cellMarked) {
            if(gameWon) {
                Snackbar.make(button0, "**** PLAYER " + game.getCurrentPlayer() + " WON ! ****", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                updateView(game.getCurrentPlayer(), location);

                // display dialog
                /*final Dialog dialog = new Dialog(getActivity());
                dialog.setContentView(R.layout.dialog_game_won);
                ImageView img = (ImageView) dialog.findViewById(R.id.imageView);
                if(game.getCurrentPlayer() != me)
                    img.setImageResource(R.drawable.loser2);
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        startNewGame();
                    }
                });
                dialog.show();*/

            }
            else{
                updateView(game.getCurrentPlayer(), location);
                int newGamer = game.changePlayer();
//                Snackbar.make(button0, "NOW GAMER " + newGamer, Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        }
        else {
//            Snackbar.make(button0, "select another cell", Snackbar.LENGTH_LONG)
//                    .setAction("Action", null).show();
        }
    }

}
