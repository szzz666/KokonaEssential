package top.szzz666.KokonaEssential.listener;

import top.szzz666.KokonaEssential.command.QQCommandManage;
import top.szzz666.qq.entity.Event;
import top.szzz666.qq.listener.QQEventHandler;

public class QQListeners {

    @QQEventHandler
    public void onEvent(Event event) {
        if (!event.isNotMe()) return;
        String postType = event.getPost_type();
        if ("message".equals(postType)) {
            QQCommandManage.dispatch(event);
        }
    }
}
