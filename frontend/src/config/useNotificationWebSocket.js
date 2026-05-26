import { useEffect, useRef } from "react";
import { useDispatch, useSelector } from "react-redux";
import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";
import { receiveNotification } from "../api/Notification/Action";
import { getUnreadCount } from "../api/Notification/Action";
import { API_URL } from "./api";

const WS_URL = `${API_URL.replace(/\/api\/v1\/?$/, "")}/ws/notifications`;

/**
 * Custom hook that manages WebSocket connection for real-time notifications.
 * Connects when user is authenticated, disconnects on logout.
 */
export default function useNotificationWebSocket() {
  const dispatch = useDispatch();
  const { user } = useSelector((state) => state.auth);
  const clientRef = useRef(null);

  const parseNotificationPayload = (body) => {
    if (!body) return null;
    try {
      const parsed = JSON.parse(body);
      if (parsed && parsed.data) return parsed.data;
      return parsed;
    } catch {
      return null;
    }
  };

  useEffect(() => {
    if (!user || !user.id) return;

    // Fetch initial unread count
    dispatch(getUnreadCount());

    const client = new Client({
      webSocketFactory: () => new SockJS(WS_URL),
      reconnectDelay: 5000,
      heartbeatIncoming: 10000,
      heartbeatOutgoing: 10000,
      debug: (str) => {
        // Uncomment for debugging:
        // console.log("[WS]", str);
      },
      onConnect: () => {
        console.log("[Notification WS] Connected");

        // Subscribe to user-specific notification topic
        client.subscribe(
          `/topic/notifications/${user.id}`,
          (message) => {
            const notification = parseNotificationPayload(message.body);
            if (notification) {
              dispatch(receiveNotification(notification));
            } else {
              console.warn("[Notification WS] Empty payload", message.body);
            }
          }
        );
      },
      onStompError: (frame) => {
        console.error("[Notification WS] STOMP error:", frame);
      },
      onWebSocketError: (event) => {
        console.error("[Notification WS] WebSocket error:", event);
      },
      onWebSocketClose: (event) => {
        console.warn("[Notification WS] WebSocket closed:", event);
      },
      onDisconnect: () => {
        console.log("[Notification WS] Disconnected");
      },
    });

    client.activate();
    clientRef.current = client;

    return () => {
      if (clientRef.current) {
        clientRef.current.deactivate();
        clientRef.current = null;
      }
    };
  }, [user, dispatch]);

  return clientRef;
}
