package moflow.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import moflow.adapters.DisplayItemAdapter;
import moflow.dialogs.CreatureEditDialog;
import moflow.dialogs.SimpleDialogListener;
import moflow.utility.*;
import moflow.wolfpup.Creature;

import java.util.ArrayList;

/**
 * Created by alex on 10/19/14.
 */
public class InitiativeActivity extends ListActivity
        implements AdapterView.OnItemClickListener, AbsListView.MultiChoiceModeListener,
                    DialogInterface.OnClickListener, SimpleDialogListener {

    private DBTransaction dbTransaction;
    private ArrayList< Creature > initList;
    private ArrayList< Creature > deleteList;
    private ArrayList<String> partyList;
    private ArrayList<String> encounterList;
    private ArrayAdapter< Creature > listAdapter;
    private int indexOfItemToEdit;
    private CreatureEditDialog newCreatureDialog;
    private CreatureEditDialog editCreatureDialog;
    private Dialog newCreatureChoiceDialog;
    private Dialog deleteCreatureChoiceDialog;
    private Dialog partyChoiceList;
    private Dialog encounterChoiceList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setTitle("Initiative");

        dbTransaction = new DBTransaction( this );
        initList = dbTransaction.getInitiativeItems();

        // fill the list with parties from database
        listAdapter = new DisplayItemAdapter(
                this,
                R.layout.groupitemdisplay,
                initList,
                true );
        setListAdapter(listAdapter);

        getListView().setOnItemClickListener(this);
        getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        getListView().setMultiChoiceModeListener(this);

        deleteList = new ArrayList<Creature>();

        indexOfItemToEdit = -1;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("New Creature")
                .setItems(R.array.newCatalogCreatureDialogChoices, this);
        newCreatureChoiceDialog = builder.create();

        partyList = dbTransaction.getGroupList(Key.Val.PARTY);
        String [] pList = partyList.toArray(new String[partyList.size()]);
        builder = new AlertDialog.Builder(this)
                .setTitle("Parties")
                .setItems(pList, this);
        partyChoiceList = builder.create();

        encounterList = dbTransaction.getGroupList(Key.Val.ENCOUNTER);
        String [] eList = encounterList.toArray(new String[encounterList.size()]);
        builder = new AlertDialog.Builder(this)
                .setTitle("Encounters")
                .setItems(eList, this);
        encounterChoiceList = builder.create();
    }

    // Inflates the action bar.
    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate( R.menu.actionbar_initiative, menu );
        return super.onCreateOptionsMenu( menu );
    }

    // Handles action bar menu item selection.
    @Override
    public boolean onOptionsItemSelected( MenuItem item ) {
        // Handle presses on the action bar items
        switch ( item.getItemId() ) {
            case R.id.action_new:
                newCreatureChoiceDialog.show();
                break;
            case R.id.action_nextItem:
                break;
            case R.id.action_prevItem:
                break;
            case R.id.action_rollInit:
                break;
            case R.id.action_sortInitList:
                break;
            case R.id.action_waitList:
                break;
            case R.id.action_group_delete:
                break;
            case R.id.action_help:
                Toast.makeText(this, getString(R.string.initHelpMsg), Toast.LENGTH_LONG).show();
                break;
            default:
                return super.onOptionsItemSelected( item );
        }
        return false;
    }

    // Handle listview item clicks.
    @Override
    public void onItemClick(AdapterView<?> listView, View view, int position, long id) {

    }

    // Contextual action mode: checked state changed.
    @Override
    public void onItemCheckedStateChanged(ActionMode actionMode, int i, long l, boolean b) {

    }

    // Contextual action mode: setup the list adapter and inflate the view.
    @Override
    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
        return false;
    }

    // Contextual action mode: do nothing.
    @Override
    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
        return false;
    }

    // Contextual action mode: handle menu items.
    @Override
    public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
        return false;
    }

    // Contextual action mode: restore list.
    @Override
    public void onDestroyActionMode(ActionMode actionMode) {

    }

    // Handle contextual menu selection.
    @Override
    public void onClick(DialogInterface dialogInterface, int choiceIndex) {
        final int NEW_CREATURE = 0;
        final int IMPORT_PARTY = 1;
        final int IMPORT_ENCOUNTER = 2;
        final int IMPORT_CATALOG = 3;

        final int DELETE_PCs = 0;
        final int DELETE_MONSTERS = 1;
        final int DELETE_ALL = 2;

        if (dialogInterface == newCreatureChoiceDialog) {
            if (choiceIndex == NEW_CREATURE) {
                newCreatureDialog = CreatureEditDialog.newInstance("New Creature", null, Key.Val.USAGE_INIT_NEW_CREATURE);
                newCreatureDialog.show(getFragmentManager(), "newCreatureDialog");
            } else if (choiceIndex == IMPORT_PARTY) {
                partyChoiceList.show();
            } else if (choiceIndex == IMPORT_ENCOUNTER) {
                encounterChoiceList.show();
            }
        }

        if (dialogInterface == partyChoiceList) {
            ArrayList<Creature> tmpList =  dbTransaction.getGroupItemList(Key.Val.PARTY, partyList.get(choiceIndex));
            for(Creature c : tmpList) {
                prepForInitList(c, false, false);
                listAdapter.add(c);
                dbTransaction.insertNewCreatureIntoInitiative(c);
            }
        }

        if (dialogInterface == encounterChoiceList) {
            ArrayList<Creature> tmpList =  dbTransaction.getGroupItemList(Key.Val.ENCOUNTER, encounterList.get(choiceIndex));
            for(Creature c : tmpList) {
                prepForInitList(c, false, true);
                listAdapter.add(c);
                dbTransaction.insertNewCreatureIntoInitiative(c);
            }
        }
    }

    private void prepForInitList(Creature c, boolean isMonster, boolean randomHitDie) {
        c.setAsMonster(isMonster);
        String hitDie = c.getHitDie();
        if (HitDie.isHitDieExpression(hitDie)) {
            HitDie hpDie = new HitDie(hitDie);
            String maxHPStr;
            if (randomHitDie) {
                maxHPStr = String.valueOf(hpDie.rollHitDie());
            } else {
                maxHPStr = String.valueOf(hpDie.getMaxVal());
            }
            c.setMaxHitPoints(maxHPStr);
        }
        c.setCurrentHitPoints(c.getMaxHitPoints());
    }

    // Handle positive click for dialog fragments.
    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        if (dialog == newCreatureDialog) {
            if ( !newCreatureDialog.hasEmptyFields() ) {
                Creature critter = newCreatureDialog.getCritter();

                if (critter == null) {
                    CommonToast.invalidDieToast(this);
                    return;
                }

                String hitDieExp = critter.getMaxHitPoints();
                if (HitDie.isHitDieExpression(hitDieExp)) {
                    HitDie die = new HitDie(hitDieExp);
                    String roll = String.valueOf(die.rollHitDie());
                    critter.setMaxHitPoints(roll);
                    critter.setCurrentHitPoints(roll);
                }

                critter.setCreatureName(NameModifier.makeNameUnique2(initList, critter.getCreatureName()));
                initList.add(critter);
                dbTransaction.insertNewCreatureIntoInitiative(critter);
                listAdapter.sort(Creature.nameComparator());
                listAdapter.notifyDataSetChanged();
            } else
                CommonToast.invalidFieldToast(this);
        }
    }

    // Handle negative click for dialog fragments.
    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Key.PICK_PARTY) {
            if (resultCode == RESULT_OK) {
                Bundle bundle = data.getExtras().getBundle(Key.GROUP_PICK_BUNDLE);
                String partyPicked = bundle.getString(Key.GROUP_PICKED);
                addPickedGroup(partyPicked, Key.PICK_PARTY);
            }
        } else if (requestCode == Key.PICK_ENCOUNTER) {
            Bundle bundle = data.getExtras().getBundle(Key.GROUP_PICK_BUNDLE);
            String partyPicked = bundle.getString(Key.GROUP_PICKED);
            addPickedGroup(partyPicked, Key.PICK_ENCOUNTER);
        }
    }

    public void addPickedGroup(String groupPicked, int pickType) {
        ArrayList<Creature> groupItems = new ArrayList<Creature>();
        if (pickType == Key.PICK_PARTY) {
            groupItems = dbTransaction.getGroupItemList(Key.Val.PARTY, groupPicked);
        } else {
            groupItems = dbTransaction.getGroupItemList(Key.Val.ENCOUNTER, groupPicked);
        }

        for (Creature list : groupItems) {
            if (pickType == Key.PICK_PARTY) {
                list.setAsMonster(false);
            } else {
                list.setAsMonster(true);
            }
            listAdapter.add(list);
            listAdapter.notifyDataSetChanged();
        }
    }
}
