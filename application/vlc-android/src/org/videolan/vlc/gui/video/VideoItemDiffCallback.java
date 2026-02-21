/*
 * ************************************************************************
 *  VideoPlayerOverlayDelegate.kt
 * *************************************************************************
 * Copyright © 2020 VLC authors and VideoLAN
 * Author: Nicolas POMEPUY
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston MA 02110-1301, USA.
 * **************************************************************************
 *
 *
 */

/*
 * Modified by mhmdeveloper
 * Date: 2026-02-21
 * Changes:
 * - Paging 2 null placeholder fix
 */
package org.videolan.vlc.gui.video;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import org.videolan.medialibrary.interfaces.media.Folder;
import org.videolan.medialibrary.interfaces.media.MediaWrapper;
import org.videolan.medialibrary.interfaces.media.VideoGroup;
import org.videolan.medialibrary.media.MediaLibraryItem;

public class VideoItemDiffCallback
extends DiffUtil.ItemCallback<MediaLibraryItem> {

    @Override
    public boolean areItemsTheSame(
        @Nullable MediaLibraryItem oldItem,
        @Nullable MediaLibraryItem newItem) {

        if (oldItem == null || newItem == null)
            return oldItem == newItem;

        if (oldItem instanceof MediaWrapper && newItem instanceof MediaWrapper) {
            MediaWrapper o = (MediaWrapper) oldItem;
            MediaWrapper n = (MediaWrapper) newItem;
            return o.getType() == n.getType() && o.equals(n);
        }

        return oldItem.getItemType() == newItem.getItemType()
                && oldItem.equals(newItem);
    }

    @Override
    public boolean areContentsTheSame(
        @Nullable MediaLibraryItem oldItem,
        @Nullable MediaLibraryItem newItem) {

        if (oldItem == null || newItem == null)
            return oldItem.equals(newItem);

        if (oldItem instanceof MediaWrapper && newItem instanceof MediaWrapper) {
            MediaWrapper o = (MediaWrapper) oldItem;
            MediaWrapper n = (MediaWrapper) newItem;

            return o.getDisplayTime() == n.getDisplayTime()
                    && equalsNullable(o.getArtworkMrl(), n.getArtworkMrl())
                    && o.getSeen() == n.getSeen()
                    && o.isPresent() == n.isPresent()
                    && o.isFavorite() == n.isFavorite();
        }

        if (oldItem instanceof VideoGroup && newItem instanceof VideoGroup) {
            VideoGroup o = (VideoGroup) oldItem;
            VideoGroup n = (VideoGroup) newItem;

            return equalsNullable(o.getTitle(), n.getTitle())
                    && o.getTracksCount() == n.getTracksCount()
                    && o.getPresentCount() == n.getPresentCount()
                    && o.isFavorite() == n.isFavorite();
        }

        if (oldItem instanceof Folder && newItem instanceof Folder) {
            Folder o = (Folder) oldItem;
            Folder n = (Folder) newItem;

            return equalsNullable(o.getTitle(), n.getTitle())
                    && o.getTracksCount() == n.getTracksCount()
                    && equalsNullable(o.mMrl, n.mMrl)
                    && o.isFavorite() == n.isFavorite();
        }

        return oldItem.equals(newItem);
    }

    @Nullable
    @Override
    public Object getChangePayload(
        @Nullable MediaLibraryItem oldItem,
        @Nullable MediaLibraryItem newItem) {

        if (oldItem == null || newItem == null)
            return null;

        if (oldItem instanceof MediaWrapper && newItem instanceof MediaWrapper) {
            MediaWrapper o = (MediaWrapper) oldItem;
            MediaWrapper n = (MediaWrapper) newItem;

            if (o.getDisplayTime() != n.getDisplayTime())
                return VideoListPayloadsKt.UPDATE_TIME;
        }

        if (oldItem.getArtworkMrl() != null &&
            !oldItem.getArtworkMrl().equals(newItem.getArtworkMrl()))
            return VideoListPayloadsKt.UPDATE_THUMB;

        if (oldItem.isFavorite() != newItem.isFavorite())
            return VideoListPayloadsKt.UPDATE_FAVORITE_STATE;

        return null;
    }

    private boolean equalsNullable(Object a, Object b) {
        return a == null ? b == null : a.equals(b);
    }
}
