package com.randos.music_player.notification

import android.content.Context
import android.os.Bundle
import androidx.annotation.OptIn
import androidx.core.app.NotificationCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.media3.common.C
import androidx.media3.common.Player
import androidx.media3.common.util.Assertions
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.CommandButton
import androidx.media3.session.DefaultMediaNotificationProvider
import androidx.media3.session.MediaNotification
import androidx.media3.session.MediaSession
import androidx.media3.session.SessionCommand
import com.google.common.collect.ImmutableList
import com.randos.musicplayer.R
import java.util.Arrays

/**
 * Custom notification provider, it has five action buttons:
 * - Shuffle
 * - Previous
 * - Play/Pause
 * - Next
 * - Repeat
 */

//TODO Implement notification provider such that it provides shuffle and repeat functionality
@OptIn(UnstableApi::class)
class MusicVibeMediaNotificationProvider(private val context: Context) :
    DefaultMediaNotificationProvider(context) {

    override fun addNotificationActions(
        mediaSession: MediaSession,
        mediaButtons: ImmutableList<CommandButton>,
        builder: NotificationCompat.Builder,
        actionFactory: MediaNotification.ActionFactory
    ): IntArray {
        var compactViewIndices = IntArray(5)
        val defaultCompactViewIndices = IntArray(5)
        Arrays.fill(compactViewIndices, C.INDEX_UNSET)
        Arrays.fill(defaultCompactViewIndices, C.INDEX_UNSET)
        var compactViewCommandCount = 0
        for (i in mediaButtons.indices) {
            val commandButton = mediaButtons[i]
            if (commandButton.sessionCommand != null) {
                builder.addAction(
                    actionFactory.createCustomActionFromCustomCommandButton(
                        mediaSession,
                        commandButton
                    )
                )
            } else {
                Assertions.checkState(commandButton.playerCommand != Player.COMMAND_INVALID)
                builder.addAction(
                    actionFactory.createMediaAction(
                        mediaSession,
                        IconCompat.createWithResource(context, commandButton.iconResId),
                        commandButton.displayName,
                        commandButton.playerCommand
                    )
                )
            }
            if (compactViewCommandCount == 5) {
                continue
            }
            val compactViewIndex = commandButton.extras.getInt(
                COMMAND_KEY_COMPACT_VIEW_INDEX,  /* defaultValue= */C.INDEX_UNSET
            )
            if (compactViewIndex >= 0 && compactViewIndex < compactViewIndices.size) {
                compactViewCommandCount++
                compactViewIndices[compactViewIndex] = i

            } else if (commandButton.playerCommand == Player.COMMAND_SET_SHUFFLE_MODE
            ) {
                defaultCompactViewIndices[0] = i
            } else if (commandButton.playerCommand == Player.COMMAND_SEEK_TO_PREVIOUS
                || commandButton.playerCommand == Player.COMMAND_SEEK_TO_PREVIOUS_MEDIA_ITEM
            ) {
                defaultCompactViewIndices[1] = i
            } else if (commandButton.playerCommand == Player.COMMAND_PLAY_PAUSE) {
                defaultCompactViewIndices[2] = i
            } else if (commandButton.playerCommand == Player.COMMAND_SEEK_TO_NEXT
                || commandButton.playerCommand == Player.COMMAND_SEEK_TO_NEXT_MEDIA_ITEM
            ) {
                defaultCompactViewIndices[3] = i
            }else if (commandButton.playerCommand == Player.COMMAND_SET_REPEAT_MODE
            ) {
                defaultCompactViewIndices[4] = i
            }
        }
        if (compactViewCommandCount == 0) {
            // If there is no custom configuration we use the seekPrev (if any), play/pause (if any),
            // seekNext (if any) action in compact view.
            var indexInCompactViewIndices = 0
            for (i in defaultCompactViewIndices.indices) {
                if (defaultCompactViewIndices[i] == C.INDEX_UNSET) {
                    continue
                }
                compactViewIndices[indexInCompactViewIndices] = defaultCompactViewIndices[i]
                indexInCompactViewIndices++
            }
        }
        for (i in compactViewIndices.indices) {
            if (compactViewIndices[i] == C.INDEX_UNSET) {
                compactViewIndices = compactViewIndices.copyOf(i)
                break
            }
        }
        return compactViewIndices
    }

    override fun getMediaButtons(
        session: MediaSession,
        playerCommands: Player.Commands,
        customLayout: ImmutableList<CommandButton>,
        showPauseButton: Boolean
    ): ImmutableList<CommandButton> {
        return getMediaButtonss(session, playerCommands, customLayout, showPauseButton)
    }

    protected fun getMediaButtonss(
        session: MediaSession?,
        playerCommands: Player.Commands,
        customLayout: ImmutableList<CommandButton>,
        showPauseButton: Boolean
    ): ImmutableList<CommandButton> {
        // Skip to previous action.
        val commandButtons = ImmutableList.Builder<CommandButton>()

        if (playerCommands.containsAny(
                Player.COMMAND_SET_SHUFFLE_MODE
            )
        ) {
            val commandButtonExtras = Bundle()
            commandButtonExtras.putInt(COMMAND_KEY_COMPACT_VIEW_INDEX, C.INDEX_UNSET)
            commandButtons.add(
                CommandButton.Builder()
                    .setPlayerCommand(Player.COMMAND_SET_SHUFFLE_MODE)
                    .setIconResId(R.drawable.round_shuffle_enabled)
                    .setDisplayName(
                        "media3_controls_shuffle"
                    )
                    .setExtras(commandButtonExtras)
                    .build()
            )
        }

        if (playerCommands.containsAny(
                Player.COMMAND_SEEK_TO_PREVIOUS,
                Player.COMMAND_SEEK_TO_PREVIOUS_MEDIA_ITEM
            )
        ) {
            val commandButtonExtras = Bundle()
            commandButtonExtras.putInt(COMMAND_KEY_COMPACT_VIEW_INDEX, C.INDEX_UNSET)
            commandButtons.add(
                CommandButton.Builder()
                    .setPlayerCommand(Player.COMMAND_SEEK_TO_PREVIOUS_MEDIA_ITEM)
                    .setIconResId(R.drawable.round_skip_previous)
                    .setDisplayName(
                        "media3_controls_seek_to_previous_description"
                    )
                    .setExtras(commandButtonExtras)
                    .build()
            )
        }
        if (playerCommands.contains(Player.COMMAND_PLAY_PAUSE)) {
            val commandButtonExtras = Bundle()
            commandButtonExtras.putInt(COMMAND_KEY_COMPACT_VIEW_INDEX, C.INDEX_UNSET)
            commandButtons.add(
                CommandButton.Builder()
                    .setPlayerCommand(Player.COMMAND_PLAY_PAUSE)
                    .setIconResId(
                        if (showPauseButton) R.drawable.round_pause else R.drawable.round_play_arrow
                    )
                    .setExtras(commandButtonExtras)
                    .setDisplayName(
                        if (showPauseButton) "R.string.media3_controls_pause_description"
                        else "R.string.media3_controls_play_description"
                    )
                    .build()
            )
        }
        // Skip to next action.
        if (playerCommands.containsAny(
                Player.COMMAND_SEEK_TO_NEXT,
                Player.COMMAND_SEEK_TO_NEXT_MEDIA_ITEM
            )
        ) {
            val commandButtonExtras = Bundle()
            commandButtonExtras.putInt(COMMAND_KEY_COMPACT_VIEW_INDEX, C.INDEX_UNSET)
            commandButtons.add(
                CommandButton.Builder()
                    .setPlayerCommand(Player.COMMAND_SEEK_TO_NEXT_MEDIA_ITEM)
                    .setIconResId(R.drawable.round_skip_next)
                    .setExtras(commandButtonExtras)
                    .setDisplayName("media3_controls_seek_to_next_description")
                    .build()
            )
        }

        // Skip to next action.
        if (playerCommands.containsAny(
                Player.COMMAND_SET_REPEAT_MODE
            )
        ) {
            val commandButtonExtras = Bundle()
            commandButtonExtras.putInt(COMMAND_KEY_COMPACT_VIEW_INDEX, C.INDEX_UNSET)
            commandButtons.add(
                CommandButton.Builder()
                    .setPlayerCommand(Player.COMMAND_SET_REPEAT_MODE)
                    .setIconResId(R.drawable.round_repeat_all)
                    .setExtras(commandButtonExtras)
                    .setDisplayName("media3_controls_repeat")
                    .build()
            )
        }


        for (i in customLayout.indices) {
            val button = customLayout[i]
            if (button.sessionCommand != null
                && button.sessionCommand!!.commandCode == SessionCommand.COMMAND_CODE_CUSTOM
            ) {
                commandButtons.add(button)
            }
        }
        return commandButtons.build()
    }
}