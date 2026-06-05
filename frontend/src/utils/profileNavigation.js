export const navigateToUserProfile = (
  navigate,
  currentUserId,
  targetUserId,
) => {
  const current = Number(currentUserId);
  const target = Number(targetUserId);

  if (!target) {
    return;
  }

  if (current && current === target) {
    navigate("/profile");
    return;
  }

  navigate(`/users/${target}`);
};
