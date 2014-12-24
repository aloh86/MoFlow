package moflow.activities;

import android.app.*;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.SparseBooleanArray;
import android.view.*;
import android.widget.*;
import moflow.adapters.DisplayItemAdapter;
import moflow.dialogs.CreatureEditDialog;
import moflow.dialogs.SimpleDialogListener;
import moflow.utility.*;
import moflow.wolfpup.Creature;

import java.util.*;

/**
 * Created by alex on 10/19/14.
 */
public class InitiativeActivity extends ListActivity
        implements AdapterView.OnItemClickListener, AbsListView.MultiChoiceModeListener,
                    DialogInterface.OnClickListener, SimpleDialogListener {

    private DBTransaction dbTransaction;
    private ArrayList< Creature > initList;
    private ArrayList< Creature > deleteList;
    private ArrayList<Creature> waitList;
    private ArrayList<String> partyList;
    private ArrayList<String> encounterList;
    private ArrayAdapter< Creature > listAdapter;
    private int indexOfItemToEdit;
    private int indexOfItemToHold;
    private int indexOfHasInit;
    private int initRound;
    private CreatureEditDialog newCreatureDialog;
    private CreatureEditDialog editCreatureDialog;
    private Dialog newCreatureChoiceDialog;
    private Dialog deleteCreatureChoiceDialog;
    private Dialog partyChoiceDialog;
    private Dialog encounterChoiceDialog;
    private Dialog rollChoiceDialog;
    private Dialog sortChoiceDialog;
    private Dialog waitItemsDialog;
    private Dialog surpriseDialog;
    private final int PCs = 0;
    private final int MONSTERS = 1;
    private final int ALL = 2;
    private final int DESCENDING = 0;
    private final int ASCENDING = 1;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        this.setTitle("Round: ");

        dbTransaction = new DBTransaction(this);
        initList = dbTransaction.getInitiativeItems();

        // fill the list with parties from database
        listAdapter = new DisplayItemAdapter(this, R.layout.groupitemdisplay, initList, true);
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
        partyChoiceDialog = builder.create();

        encounterList = dbTransaction.getGroupList(Key.Val.ENCOUNTER);
        String [] eList = encounterList.toArray(new String[encounterList.size()]);
        builder = new AlertDialog.Builder(this)
                .setTitle("Encounters")
                .setItems(eList, this);
        encounterChoiceDialog = builder.create();

        builder = new AlertDialog.Builder(this);
        builder.setTitle("Remove")
                .setItems(R.array.pc_monster_all, this);
        deleteCreatureChoiceDialog = builder.create();

        builder = new AlertDialog.Builder(this);
        builder.setTitle("Roll Init")
                .setItems(R.array.pc_monster_all, this);
        rollChoiceDialog = builder.create();

        builder = new AlertDialog.Builder(this);
        builder.setTitle("Sort")
                .setItems(R.array.sortChoiceArray, this);
        sortChoiceDialog = builder.create();

        builder = new AlertDialog.Builder(this);
        builder.setTitle("Surprise Round")
                .setMessage("Start with surprise round?")
                .setNegativeButton("No", this)
                .setPositiveButton("Yes", this);
        surpriseDialog = builder.create();

        waitList = new ArrayList<Creature>();

        indexOfHasInit = -1;
        initRound = -1;
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        newCreatureChoiceDialog.dismiss();
        deleteCreatureChoiceDialog.dismiss();
        partyChoiceDialog.dismiss();
        encounterChoiceDialog.dismiss();
        rollChoiceDialog.dismiss();
        sortChoiceDialog.dismiss();

        if (null != waitItemsDialog)
            waitItemsDialog.dismiss();

        surpriseDialog.dismiss();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        if (!initList.isEmpty()) {
            Parcelable [] initCreatures = new Parcelable[initList.size()];
            for (int i = 0; i < initList.size(); i++) {
                initCreatures[i] = initList.get(i);
            }
            outState.putParcelableArray("initList", initCreatures);
        }

        if (!deleteList.isEmpty()) {
            Parcelable [] deleteCreatures = new Parcelable[deleteList.size()];
            for (int i = 0; i < deleteList.size(); i++) {
                deleteCreatures[i] = deleteList.get(i);
            }
            outState.putParcelableArray("deleteList", deleteCreatures);
        }

        if (!waitList.isEmpty()) {
            Parcelable [] waitCreatures = new Parcelable[waitList.size()];
            for (int i = 0; i < waitList.size(); i++) {
                waitCreatures[i] = waitList.get(i);
            }
            outState.putParcelableArray("waitList", waitCreatures);
        }

        outState.putInt("hasInitIndex", indexOfHasInit);
        outState.putInt("editIndex", indexOfItemToEdit);
        outState.putInt("holdIndex", indexOfItemToHold);
        outState.putInt("round", initRound);
    }

    @Override
    protected void onRestoreInstanceState(Bundle inState)
    {
        super.onRestoreInstanceState(inState);

        FragmentManager fm = getFragmentManager();

        if (null != fm.findFragmentByTag("editCreatureDialog"))
            editCreatureDialog = (CreatureEditDialog) fm.findFragmentByTag("editCreatureDialog");

        if (null != fm.findFragmentByTag("newCreatureDialog"))
            newCreatureDialog = (CreatureEditDialog) fm.findFragmentByTag("newCreatureDialog");

        if (inState.containsKey("initList")) {
            initList.clear();
            Parcelable [] initCreatureList = inState.getParcelableArray("initList");
            for (int i = 0; i < initCreatureList.length; i++) {
                initList.add((Creature)initCreatureList[i]);
            }
        }

        if (inState.containsKey("deleteList")) {
            deleteList.clear();
            Parcelable [] initCreatureDeleteList = inState.getParcelableArray("deleteList");

            for (int j = 0; j < initCreatureDeleteList.length; j++) {
                int indexToCheck = getItemPosition((Creature)initCreatureDeleteList[j]);
                if (indexToCheck != -1) {
                    getListView().setItemChecked(indexToCheck, true);
                }
            }
        }

        if (inState.containsKey("waitList")) {
            waitList.clear();
            Parcelable [] initCreatureWaitList = inState.getParcelableArray("waitList");
            for (int i = 0; i < initCreatureWaitList.length; i++) {
                waitList.add((Creature)initCreatureWaitList[i]);
            }
        }

        indexOfHasInit = inState.getInt("hasInitIndex");
        indexOfItemToEdit = inState.getInt("editIndex");
        indexOfItemToHold = inState.getInt("holdIndex");
        initRound = inState.getInt("round");
        updateRoundTitle();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        saveInitList();
    }

    // Inflates the action bar.
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbar_initiative, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // Handles action bar menu item selection.
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_new:
                newCreatureChoiceDialog.show();
                break;
            case R.id.action_nextItem:
                giveNextCreatureInit();
                break;
            case R.id.action_prevItem:
                givePrevCreatureInit();
                break;
            case R.id.action_rollInit:
                rollChoiceDialog.show();
                break;
            case R.id.action_sortInitList:
                sortChoiceDialog.show();
                break;
            case R.id.action_waitList:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                String [] wList = getWaitListNameArray(waitList);
                builder.setTitle("Wait List")
                        .setItems(wList, this);
                waitItemsDialog = builder.create();
                waitItemsDialog.show();
                break;
            case R.id.action_group_delete:
                deleteCreatureChoiceDialog.show();
                break;
            case R.id.action_reset_init:
                resetInitiative();
                deleteList.clear();
                waitList.clear();
                break;
            case R.id.action_help:
                Toast.makeText(this, getString(R.string.initHelpMsg), Toast.LENGTH_LONG).show();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return false;
    }

    // Handle listview item clicks.
    @Override
    public void onItemClick(AdapterView<?> listView, View view, int position, long id)
    {
        String nameAsTitle = initList.get(position).getCreatureName();
        editCreatureDialog = CreatureEditDialog.newInstance(nameAsTitle, initList.get(position), Key.Val.USAGE_INIT_EDIT_CREATURE);
        editCreatureDialog.show(getFragmentManager(), "editCreatureDialog");
        indexOfItemToEdit = position;
    }

    // Contextual action mode: checked state changed.
    @Override
    public void onItemCheckedStateChanged(ActionMode actionMode, int position, long id, boolean checked)
    {
        if (checked)
            deleteList.add(listAdapter.getItem(position));
        else
            deleteList.remove(listAdapter.getItem(position));

        MenuItem item = actionMode.getMenu().findItem( R.id.action_hold );

        if (deleteList.size() == 1 && deleteList.get(0).hasInit()) {
            SparseBooleanArray checkedItems = getListView().getCheckedItemPositions();
            for (int i = 0; i < checkedItems.size(); i++) {
                if (checkedItems.valueAt(i)) {
                    Creature c = initList.get(checkedItems.keyAt(i));
                    if (c == deleteList.get(0)) {
                        item.setEnabled(true);
                        indexOfItemToHold = indexOfHasInit;
                    }
                }
            }
        } else {
            item.setEnabled(false);
        }
    }

    // Contextual action mode: setup the list adapter and inflate the view.
    @Override
    public boolean onCreateActionMode(ActionMode actionMode, Menu menu)
    {
        listAdapter = null;
        listAdapter = new ArrayAdapter<Creature>(
                this,
                android.R.layout.simple_list_item_checked,
                initList);
        setListAdapter(listAdapter);
        MenuInflater inflater = actionMode.getMenuInflater();
        inflater.inflate(R.menu.context_actionbar_initiative, menu);

        return true;
    }

    // Contextual action mode: do nothing.
    @Override
    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {

        return false;
    }

    // Contextual action mode: handle menu items.
    @Override
    public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem)
    {
        switch (menuItem.getItemId()) {
            case R.id.action_discard:
                deleteSelectedItems();
                listAdapter.notifyDataSetChanged();
                actionMode.finish();
                return true;
            case R.id.action_hold:
                holdCreature();
                listAdapter.notifyDataSetChanged();
                actionMode.finish();
                return true;
        }
        return false;
    }

    // Contextual action mode: restore list.
    @Override
    public void onDestroyActionMode(ActionMode actionMode)
    {
        deleteList.clear();

        listAdapter = null;
        listAdapter = new DisplayItemAdapter(this, R.layout.groupitemdisplay, initList, true );
        setListAdapter(listAdapter);
    }

    private void deleteSelectedItems()
    {
        dbTransaction.deleteCreatureFromInitiative(deleteList);
        for (Creature c : deleteList) {
            listAdapter.remove( c );
        }
    }

    // Handle contextual menu selection.
    @Override
    public void onClick(DialogInterface dialogInterface, int choiceIndex)
    {
        final int NEW_CREATURE = 0;
        final int IMPORT_PARTY = 1;
        final int IMPORT_ENCOUNTER = 2;
        final int IMPORT_CATALOG = 3;

        if (dialogInterface == newCreatureChoiceDialog) {
            if (choiceIndex == NEW_CREATURE) {
                newCreatureDialog = CreatureEditDialog.newInstance("New Creature", null, Key.Val.USAGE_INIT_NEW_CREATURE);
                newCreatureDialog.show(getFragmentManager(), "newCreatureDialog");
            } else if (choiceIndex == IMPORT_PARTY) {
                partyChoiceDialog.show();
            } else if (choiceIndex == IMPORT_ENCOUNTER) {
                encounterChoiceDialog.show();
            } else if (choiceIndex == IMPORT_CATALOG) {
                Intent intent = new Intent("moflow.activities.CatalogActivity");
                intent.putExtra(Key.PARENT_ACTIVITY, Key.Val.INITIATIVE_ACTIVITY);
                startActivityForResult(intent, Key.PICK_CREATURE);
            }
        }

        if (dialogInterface == partyChoiceDialog) {
            ArrayList<Creature> tmpList =  dbTransaction.getGroupItemList(Key.Val.PARTY, partyList.get(choiceIndex));
            for(Creature c : tmpList) {
                prepForInitList(c, false, false);
                listAdapter.add(c);
                dbTransaction.insertNewCreatureIntoInitiative(c);
            }
        }

        if (dialogInterface == encounterChoiceDialog) {
            ArrayList<Creature> tmpList =  dbTransaction.getGroupItemList(Key.Val.ENCOUNTER, encounterList.get(choiceIndex));
            for(Creature c : tmpList) {
                prepForInitList(c, true, true);
                listAdapter.add(c);
                dbTransaction.insertNewCreatureIntoInitiative(c);
            }
        }

        if (dialogInterface == deleteCreatureChoiceDialog) {
            if (choiceIndex == PCs) {
                dbTransaction.deletePCsFromInitiative();
                for (Iterator<Creature> iterator = initList.iterator(); iterator.hasNext();) {
                    Creature c = iterator.next();
                    if (!c.isMonster()) {
                        iterator.remove();
                    }
                }
            } else if (choiceIndex == MONSTERS) {
                dbTransaction.deleteMonstersFromInitiative();
                for (Iterator<Creature> iterator = initList.iterator(); iterator.hasNext();) {
                    Creature c = iterator.next();
                    if (c.isMonster()) {
                        iterator.remove();
                    }
                }
            } else if (choiceIndex == ALL) {
                dbTransaction.deleteAllFromInitiative();
                initList.clear();
                deleteList.clear();
                waitList.clear();
            }
        }

        if (dialogInterface == rollChoiceDialog) {
            if (choiceIndex == PCs) {
                rollInit(PCs);
            } else if (choiceIndex == MONSTERS) {
                rollInit(MONSTERS);
            } else if (choiceIndex == ALL) {
                rollInit(ALL);
            }

        }

        if (dialogInterface == sortChoiceDialog) {
            if (choiceIndex == DESCENDING) {
                sortInitiative(DESCENDING);
            } else if (choiceIndex == ASCENDING) {
                sortInitiative(ASCENDING);
            }
        }

        if (dialogInterface == waitItemsDialog) {
            Creature creature = waitList.get(choiceIndex);
            Creature creatureWithInit = getCreatureWithInit();

            if (null == creatureWithInit) {
                Toast.makeText(this, "Start initiative first.", Toast.LENGTH_LONG).show();
                return;
            }

            initList.add(indexOfHasInit, creature);
            creature.setHasInit(true);

            if (creatureWithInit != null)
                creatureWithInit.setHasInit(false);

            waitList.remove(choiceIndex);
        }

        if (dialogInterface == surpriseDialog) {
            if (choiceIndex == Dialog.BUTTON_POSITIVE) {
                initRound = 0;
            } else if (choiceIndex == Dialog.BUTTON_NEGATIVE) {
                initRound = 1;
            }
            indexOfHasInit = 0;
            initList.get(indexOfHasInit).setHasInit(true);
            updateRoundTitle();
        }

        listAdapter.notifyDataSetChanged();
    }

    private void prepForInitList(Creature c, boolean isMonster, boolean randomHitDie)
    {
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

        String name = NameModifier.makeNameUnique2(initList, c.getCreatureName());
        c.setCreatureName(name);
    }

    // Handle positive click for dialog fragments.
    @Override
    public void onDialogPositiveClick(DialogFragment dialog)
    {
        if (dialog == newCreatureDialog) {
            if ( !newCreatureDialog.hasEmptyFields() ) {
                Creature critter = newCreatureDialog.getCritter();

                if (critter == null) {
                    CommonToast.invalidDieToast(this);
                    return;
                }

                String hitDieExp = critter.getHitDie();
                String result = hitDieExpToInt(hitDieExp);
                if (result != null) {
                    critter.setMaxHitPoints(result);
                    critter.setCurrentHitPoints(result);
                }

                critter.setCreatureName(NameModifier.makeNameUnique2(initList, critter.getCreatureName()));
                initList.add(critter);
                dbTransaction.insertNewCreatureIntoInitiative(critter);
                listAdapter.notifyDataSetChanged();
            } else
                CommonToast.invalidFieldToast(this);
        }

        if ( dialog == editCreatureDialog ) {
            if ( !editCreatureDialog.hasEmptyFields() ) {
                Creature thing = editCreatureDialog.getCritter();

                if (thing == null) {
                    CommonToast.invalidDieToast(this);
                    return;
                }

                if ( thing.equals(initList.get(indexOfItemToEdit))) {
                    return;
                } else {
                    String oldName = initList.get(indexOfItemToEdit).getCreatureName();

                    if ( !oldName.equals( thing.getCreatureName() ) )
                        thing.setCreatureName( NameModifier.makeNameUnique2( initList, thing.getCreatureName() ) );

                    initList.set( indexOfItemToEdit, thing );
                    dbTransaction.updateCreatureInInit(thing, oldName);
                    listAdapter.notifyDataSetChanged();
                }
            } else
                CommonToast.invalidFieldToast(this);
        }
    }

    // Handle negative click for dialog fragments.
    @Override
    public void onDialogNegativeClick(DialogFragment dialog)
    {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Key.PICK_CREATURE) {
            if (resultCode == RESULT_OK) {
                Bundle bundle = data.getExtras().getBundle(Key.NUMPICK_CATALOG_CREATURE_BUNDLE);
                int numPicked = bundle.getInt(Key.NUM__CREATURE_PICKED);
                Creature pickedCreature = bundle.getParcelable(Key.CREATURE_OBJECT);
                addPickedCreature(numPicked, pickedCreature);
            }
        }
    }

    private void addPickedCreature(int numPicked, Creature pickedCreature)
    {
        for (int i = 0; i < numPicked; i++) {
            Creature copy = pickedCreature.clone();
            copy.setAsMonster(true);
            copy.setCreatureName(NameModifier.makeNameUnique2(initList, copy.getCreatureName()));

            String hitDieExp = copy.getHitDie();
            String result = hitDieExpToInt(hitDieExp);
            if (result != null) {
                copy.setMaxHitPoints(result);
                copy.setCurrentHitPoints(result);
            } 

            listAdapter.add(copy);
            listAdapter.notifyDataSetChanged();
            dbTransaction.insertNewCreatureIntoInitiative(copy);
        }
    }

    /**
     * Get a integer value as a string from a hit die expression
     * @param hitDieExpression hit die expression (ex. 3d4).
     * @return A string representing an integer value. If an integer was already
     */
    private String hitDieExpToInt(String hitDieExpression)
    {
        if (HitDie.isHitDieExpression(hitDieExpression)) {
            HitDie die = new HitDie(hitDieExpression);
            return String.valueOf(die.rollHitDie());
        }

        return null;
    }


    private void rollInit(int choice)
    {
        for (Creature c : initList) {
            int mod = Integer.valueOf(c.getInitMod());
            int roll = rollInitiative();
            int result = roll + mod;
            String resultStr = String.valueOf(result);

            if (choice == ALL)
                c.setInitiative(resultStr);
            else if (choice == PCs && !c.isMonster())
                c.setInitiative(resultStr);
            else if (choice == MONSTERS && c.isMonster())
                c.setInitiative(resultStr);
        }
    }

    private int rollInitiative()
    {
        Random r = new Random();
        int min = 1;
        int range = 20;

        return r.nextInt(range) + min;
    }

    private void sortInitiative(int sortType)
    {
        Collections.sort(initList, Collections.reverseOrder());

        if (sortType == ASCENDING) {
            Collections.sort(initList);
        }
    }

    private Creature getCreatureWithInit()
    {
        for (Creature c : initList) {
            if (c.hasInit()) {
                return c;
            }
        }
        return null;
    }

    private void giveNextCreatureInit()
    {
        if (initList.isEmpty()) {
            return;
        }

        if (initRound < 0) {
            surpriseDialog.show();
            return;
        }

        initList.get(indexOfHasInit).setHasInit(false);

        // if we haven't reached the end of the list, give initiative to next item.
        if (indexOfHasInit != initList.size() - 1) {
            ++indexOfHasInit;
            initList.get(indexOfHasInit).setHasInit(true);
        } else {
            indexOfHasInit = 0;
            initList.get(indexOfHasInit).setHasInit(true);
            ++initRound;
            updateRoundTitle();
        }

        getListView().smoothScrollToPosition(indexOfHasInit);
        listAdapter.notifyDataSetChanged();
    }

    private void givePrevCreatureInit()
    {
        if (initList.isEmpty() || initRound < 0) {
            return;
        }

        if (!initList.isEmpty() && indexOfHasInit == 0 && initRound <= 0) {
            return;
        }

        initList.get(indexOfHasInit).setHasInit(false);

        if (indexOfHasInit == 0 && initRound > 0) {
            indexOfHasInit = initList.size() - 1;
            initList.get(indexOfHasInit).setHasInit(true);
            --initRound;
            updateRoundTitle();
        } else {
            --indexOfHasInit;
            initList.get(indexOfHasInit).setHasInit(true);
        }

        getListView().smoothScrollToPosition(indexOfHasInit);
        listAdapter.notifyDataSetChanged();
    }

    private String [] getWaitListNameArray(ArrayList<Creature> list)
    {
        String [] names = new String[list.size()];
        for (int i = 0; i < names.length; i++) {
            names[i] = list.get(i).getCreatureName();
        }
        return names;
    }

    private void updateRoundTitle()
    {
        String round = "";
        if (initRound < 0) round = "";
        if (initRound == 0) round = "Surprise";
        if (initRound > 0) round = String.valueOf(initRound);

        this.setTitle("Round: " + round);
    }

    private void holdCreature()
    {
        Creature tmp = initList.get(indexOfItemToHold);

        if (tmp.hasInit()) {
            if (indexOfItemToHold != initList.size() - 1) {
                tmp.setHasInit(false);
                initList.get(indexOfHasInit + 1).setHasInit(true);
            } else {
                indexOfHasInit = 0;
                initList.get(indexOfHasInit).setHasInit(true);
                ++initRound;
                updateRoundTitle();
            }
        }
        waitList.add(tmp);
        initList.remove(indexOfItemToHold);
    }

    private void saveInitList()
    {
        for (Creature c : initList) {
            dbTransaction.updateCreatureInInit(c, c.getCreatureName());
        }
    }

    private int getItemPosition(Creature c)
    {
        for (int pos = 0; pos < initList.size(); pos++) {
            if (c == initList.get(pos)) {
                return pos;
            }
        }
        return -1;
    }

    private void resetInitiative()
    {
        if (initList.isEmpty() && indexOfHasInit < 0)
            return;

        if (!initList.isEmpty() && indexOfHasInit >= 0)
            initList.get(indexOfHasInit).setHasInit(false);

        initRound = -1;
        indexOfItemToEdit = -1;
        indexOfItemToHold = -1;
        indexOfHasInit = -1;
        updateRoundTitle();
        listAdapter.notifyDataSetChanged();
    }
}