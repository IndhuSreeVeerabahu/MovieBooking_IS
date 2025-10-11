# Professional Notification System Guide

## Overview
The Professional Notification System provides beautiful, animated notifications for success, error, warning, and info messages throughout the application. It replaces basic browser alerts with a modern, user-friendly interface.

## Features
- ✅ **Beautiful Design**: Modern glass-morphism design with smooth animations
- ✅ **Multiple Types**: Success, Error, Warning, and Info notifications
- ✅ **Auto-dismiss**: Configurable auto-hide duration
- ✅ **Persistent**: Option to keep notifications until manually closed
- ✅ **Action Buttons**: Add custom action buttons to notifications
- ✅ **Responsive**: Works perfectly on mobile and desktop
- ✅ **Accessible**: Keyboard support and proper ARIA attributes
- ✅ **Customizable**: Easy to customize colors, duration, and behavior

## Quick Start

### 1. Include the Script
```html
<script src="/js/notification-system.js"></script>
```

### 2. Basic Usage
```javascript
// Success notification
window.notificationSystem.success('Operation completed successfully!');

// Error notification
window.notificationSystem.error('Something went wrong!');

// Warning notification
window.notificationSystem.warning('Please review your input!');

// Info notification
window.notificationSystem.info('This is an informational message!');
```

## Advanced Usage

### Custom Configuration
```javascript
window.notificationSystem.success('Theater added successfully!', {
    title: 'Theater Added',
    duration: 4000,
    showCloseButton: true,
    actions: [
        { id: 'view', text: 'View Details', primary: true },
        { id: 'close', text: 'Close', primary: false }
    ]
});
```

### Persistent Notifications
```javascript
window.notificationSystem.warning('System maintenance in 1 hour!', {
    title: 'Maintenance Notice',
    duration: 0, // Never auto-hide
    showCloseButton: true
});
```

### Network Error with Retry
```javascript
window.notificationSystem.error('Network error. Please check your connection.', {
    title: 'Connection Error',
    duration: 0,
    actions: [
        { id: 'retry', text: 'Retry', primary: true },
        { id: 'close', text: 'Close', primary: false }
    ]
});
```

## Specialized Methods

### Booking Success
```javascript
window.notificationSystem.showBookingSuccess('BK-2024-001');
```

### Payment Success
```javascript
window.notificationSystem.showPaymentSuccess(1250);
```

### Network Error
```javascript
window.notificationSystem.showNetworkError();
```

### Seat Expired
```javascript
window.notificationSystem.showSeatExpired();
```

## Configuration Options

| Option | Type | Default | Description |
|--------|------|---------|-------------|
| `type` | string | 'info' | Notification type: 'success', 'error', 'warning', 'info' |
| `title` | string | Auto-generated | Custom title for the notification |
| `duration` | number | 5000 | Auto-hide duration in milliseconds (0 = persistent) |
| `showCloseButton` | boolean | true | Show close button |
| `actions` | array | [] | Array of action buttons |
| `onClose` | function | null | Callback when notification is closed |
| `onAction` | function | null | Callback when action button is clicked |

## Action Button Configuration
```javascript
actions: [
    {
        id: 'save',           // Unique identifier
        text: 'Save',         // Button text
        primary: true         // Primary button styling
    },
    {
        id: 'cancel',
        text: 'Cancel',
        primary: false
    }
]
```

## Integration Examples

### Form Submission Success
```javascript
// Replace this:
alert('Theater added successfully!');

// With this:
window.notificationSystem.success('Theater added successfully!', {
    title: 'Theater Added',
    duration: 4000
});
```

### Form Validation Error
```javascript
// Replace this:
alert('Please fill in all required fields!');

// With this:
window.notificationSystem.error('Please fill in all required fields!', {
    title: 'Validation Error',
    duration: 6000
});
```

### API Error Handling
```javascript
try {
    const response = await fetch('/api/theaters', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(theaterData)
    });

    if (response.ok) {
        window.notificationSystem.success('Theater added successfully!');
    } else {
        window.notificationSystem.error('Failed to add theater. Please try again.');
    }
} catch (error) {
    window.notificationSystem.error('Network error. Please check your connection.', {
        title: 'Connection Error',
        duration: 0,
        actions: [
            { id: 'retry', text: 'Retry', primary: true },
            { id: 'close', text: 'Close', primary: false }
        ]
    });
}
```

## Utility Methods

### Hide All Notifications
```javascript
window.notificationSystem.hideAll();
```

### Hide Specific Notification
```javascript
const notification = window.notificationSystem.success('Message');
// Later...
window.notificationSystem.hide(notification);
```

## Styling Customization

The notification system uses CSS custom properties that can be overridden:

```css
:root {
    --notification-success-color: #10B981;
    --notification-error-color: #EF4444;
    --notification-warning-color: #F59E0B;
    --notification-info-color: #3B82F6;
}
```

## Best Practices

1. **Use Appropriate Types**: Choose the right notification type for the message
2. **Keep Messages Concise**: Short, clear messages work best
3. **Set Appropriate Duration**: 
   - Success: 3-4 seconds
   - Info: 4-5 seconds
   - Warning: 5-6 seconds
   - Error: 6+ seconds or persistent
4. **Provide Actions**: For important notifications, provide action buttons
5. **Handle Network Errors**: Always provide retry options for network errors
6. **Don't Overuse**: Avoid showing too many notifications at once

## Demo Page

Visit `/notification-demo.html` to see all notification types and features in action.

## Browser Support

- ✅ Chrome 60+
- ✅ Firefox 55+
- ✅ Safari 12+
- ✅ Edge 79+

## Migration from Alert/Confirm

### Before (Basic Alerts)
```javascript
alert('Theater added successfully!');
alert('Failed to add theater');
confirm('Are you sure you want to delete this theater?');
```

### After (Professional Notifications)
```javascript
window.notificationSystem.success('Theater added successfully!');
window.notificationSystem.error('Failed to add theater');
window.notificationSystem.showConfirm('Are you sure you want to delete this theater?', {
    onConfirm: () => deleteTheater()
});
```

This creates a much more professional and user-friendly experience! 🎉
