package com.appsforlife.mynotes.listeners;

public interface DialogClickLinkListener {

    void onCopyLink(boolean isCopied);

    void onDeleteLink(boolean isDelete);

    void onClickLink(boolean isOpen);

    void onShareLink(boolean isShare);

    void onEditLink(boolean isEdit);
}
