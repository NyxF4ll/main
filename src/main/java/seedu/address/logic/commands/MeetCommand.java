package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import static seedu.address.commons.core.Messages.MESSAGE_GROUP_NOT_FOUND;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DESCRIPTION;
import static seedu.address.logic.parser.CliSyntax.PREFIX_LOCATION;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TIMESTAMP;

import java.util.List;

import seedu.address.logic.CommandHistory;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.group.Group;
import seedu.address.model.meeting.Meeting;

// @@author NyxF4ll
/**
 * Set the meeting of a group to the specified value.
 */
public class MeetCommand extends Command {

    public static final String COMMAND_WORD = "meet";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Set the meeting of the group identified "
            + "by its name to the specified value. Existing values will be overwritten by the new values.\n"
            + "Not specifying any new value will cancel the current meeting.\n"
            + "Parameters: GROUP_NAME (must be a alphanumeric string with out spaces) "
            + PREFIX_NAME + "MEETING_TITLE"
            + PREFIX_TIMESTAMP + "MEETING_TIME "
            + PREFIX_LOCATION + "MEETING_LOCATION "
            + PREFIX_DESCRIPTION + "MEETING_DESCRIPTION\n"

            + "Example: " + COMMAND_WORD + " CS2103 "
            + PREFIX_NAME + "Demo Rehearsal"
            + PREFIX_TIMESTAMP + "15-10-2017@12:31 "
            + PREFIX_LOCATION + "6 College Avenue East "
            + PREFIX_DESCRIPTION + "Meeting to prepare for the upcoming software demo";

    public static final String MESSAGE_MEET_COMMAND_SUCCESS = "%1$s group meeting titled %2$s added to scheduler";
    public static final String MESSAGE_MEETING_CANCELLED = "Meeting for group %1$s cancelled";

    public final Group group;
    public final Meeting meeting;

    /**
     * Constructor for the MeetCommand Class.
     * @param group The group to associated the meeting with.
     * @param meeting Value of the new meeting, null value for this means that there are no meetings for this group.
     */
    public MeetCommand(Group group, Meeting meeting) {
        requireNonNull(group);
        this.group = group;
        this.meeting = meeting;
    }

    @Override
    public CommandResult execute(Model model, CommandHistory history) throws CommandException {
        requireNonNull(model);

        // This must be changed after find group command is implemented.
        List<Group> groupList = model.getGroupList();

        if (!model.hasGroup(group)) {
            throw new CommandException(MESSAGE_GROUP_NOT_FOUND);
        }

        Group groupToEdit = groupList.stream().filter(group::isSameGroup).findFirst().get();
        Group editedGroup = groupToEdit.copy();

        if (meeting != null) {
            editedGroup.setMeeting(meeting);

            model.updateGroup(groupToEdit, editedGroup);

            model.commitAddressBook();
            return new CommandResult(String.format(MESSAGE_MEET_COMMAND_SUCCESS,
                    groupToEdit.getTitle().toString(), meeting.getTitle().toString()));

        } else {
            editedGroup.cancelMeeting();

            model.updateGroup(groupToEdit, editedGroup);

            model.commitAddressBook();
            return new CommandResult(String.format(MESSAGE_MEETING_CANCELLED, groupToEdit.getTitle()));
        }
    }

    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof MeetCommand)) {
            return false;
        }

        // state check
        MeetCommand e = (MeetCommand) other;
        if (meeting == null && e.meeting != null) {
            return false;
        }

        return this.group.isSameGroup(e.group)
                && (this.meeting == null || this.meeting.equals(e.meeting));
    }
}
