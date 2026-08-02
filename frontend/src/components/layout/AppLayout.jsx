import React from "react";
import TopNavigation from "./TopNavigation";
import LeftSidebar from "./LeftSidebar";
import RightSidebar from "./RightSidebar";

export default function AppLayout({ children, showRightSidebar = true }) {
  return (
    <div className="bg-gray-100 min-h-screen font-sans">
      <TopNavigation />
      <div className="flex pt-16">
        <LeftSidebar />
        <main className="flex-1 ml-64 mr-80 min-h-screen">
          <div className="p-6">{children}</div>
        </main>
        {showRightSidebar && <RightSidebar />}
      </div>
    </div>
  );
}
