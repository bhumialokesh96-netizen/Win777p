# WIN777 Admin Panel

Admin web interface for WIN777 platform management.

## Features

- User Management (view, search, ban/unban)
- Task Management (create, edit, delete tasks)
- Withdrawal Approval (approve/reject user withdrawals)
- Configuration Management (theme, banners, maintenance mode)
- Audit Logs (view admin actions)

## Setup

```bash
cd admin-panel
npm install
npm start
```

## Build for Production

```bash
npm run build
```

## Environment Variables

Create a `.env` file with:

```
REACT_APP_API_URL=http://localhost:8080
```
