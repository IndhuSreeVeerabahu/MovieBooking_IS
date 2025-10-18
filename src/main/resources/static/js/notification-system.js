/**
 * Professional Notification System
 * Provides beautiful, animated notifications for success, error, warning, and info messages
 */

class NotificationSystem {
    constructor() {
        this.notifications = [];
        this.container = null;
        this.init();
    }
    
    init() {
        this.createContainer();
        this.addStyles();
    }
    
    createContainer() {
        this.container = document.createElement('div');
        this.container.id = 'notification-container';
        this.container.className = 'fixed top-4 right-4 z-50 space-y-2';
        
        // Wait for document.body to be available
        if (document.body) {
            document.body.appendChild(this.container);
        } else {
            // If document.body is not ready, wait for DOMContentLoaded
            document.addEventListener('DOMContentLoaded', () => {
                if (document.body) {
                    document.body.appendChild(this.container);
                }
            });
        }
    }
    
    addStyles() {
        if (document.getElementById('notification-styles')) return;
        
        const style = document.createElement('style');
        style.id = 'notification-styles';
        style.textContent = `
            #notification-container {
                pointer-events: none;
            }
            
            .notification {
                pointer-events: auto;
                min-width: 320px;
                max-width: 500px;
                background: linear-gradient(135deg, rgba(26, 26, 26, 0.95) 0%, rgba(42, 42, 42, 0.95) 100%);
                backdrop-filter: blur(15px);
                border: 1px solid rgba(255, 255, 255, 0.1);
                border-radius: 12px;
                box-shadow: 0 20px 40px rgba(0, 0, 0, 0.3);
                padding: 16px 20px;
                margin-bottom: 12px;
                transform: translateX(100%);
                opacity: 0;
                transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
                position: relative;
                overflow: hidden;
            }
            
            .notification.show {
                transform: translateX(0);
                opacity: 1;
            }
            
            .notification.hide {
                transform: translateX(100%);
                opacity: 0;
            }
            
            .notification.success {
                border-left: 4px solid #10B981;
            }
            
            .notification.error {
                border-left: 4px solid #EF4444;
            }
            
            .notification.warning {
                border-left: 4px solid #F59E0B;
            }
            
            .notification.info {
                border-left: 4px solid #3B82F6;
            }
            
            .notification-header {
                display: flex;
                align-items: center;
                justify-content: space-between;
                margin-bottom: 8px;
            }
            
            .notification-icon {
                width: 24px;
                height: 24px;
                margin-right: 12px;
                flex-shrink: 0;
            }
            
            .notification-title {
                font-weight: 600;
                font-size: 16px;
                color: white;
                margin: 0;
                flex: 1;
            }
            
            .notification-close {
                background: none;
                border: none;
                color: #9CA3AF;
                cursor: pointer;
                padding: 4px;
                border-radius: 4px;
                transition: all 0.2s ease;
                margin-left: 12px;
            }
            
            .notification-close:hover {
                color: white;
                background: rgba(255, 255, 255, 0.1);
            }
            
            .notification-message {
                color: #D1D5DB;
                font-size: 14px;
                line-height: 1.5;
                margin: 0;
            }
            
            .notification-progress {
                position: absolute;
                bottom: 0;
                left: 0;
                height: 3px;
                background: rgba(255, 255, 255, 0.2);
                border-radius: 0 0 12px 12px;
                overflow: hidden;
            }
            
            .notification-progress-bar {
                height: 100%;
                background: linear-gradient(90deg, #F84464, #FF6B6B);
                border-radius: 0 0 12px 12px;
                transition: width linear;
            }
            
            .notification-actions {
                margin-top: 12px;
                display: flex;
                gap: 8px;
                justify-content: flex-end;
            }
            
            .notification-btn {
                padding: 6px 12px;
                border-radius: 6px;
                font-size: 12px;
                font-weight: 500;
                cursor: pointer;
                transition: all 0.2s ease;
                border: none;
            }
            
            .notification-btn-primary {
                background: #F84464;
                color: white;
            }
            
            .notification-btn-primary:hover {
                background: #E53E3E;
            }
            
            .notification-btn-secondary {
                background: rgba(255, 255, 255, 0.1);
                color: #D1D5DB;
                border: 1px solid rgba(255, 255, 255, 0.2);
            }
            
            .notification-btn-secondary:hover {
                background: rgba(255, 255, 255, 0.2);
                color: white;
            }
            
            @media (max-width: 640px) {
                #notification-container {
                    top: 1rem;
                    right: 1rem;
                    left: 1rem;
                }
                
                .notification {
                    min-width: auto;
                    max-width: none;
                }
            }
        `;
        document.head.appendChild(style);
    }
    
    show(message, options = {}) {
        const config = {
            type: options.type || 'info',
            title: options.title || this.getDefaultTitle(options.type),
            duration: options.duration || 5000,
            showCloseButton: options.showCloseButton !== false,
            actions: options.actions || [],
            onClose: options.onClose || null,
            onAction: options.onAction || null
        };
        
        const notification = this.createNotification(message, config);
        
        // Ensure container exists before appending
        if (this.container && document.body.contains(this.container)) {
            this.container.appendChild(notification);
        } else {
            // If container doesn't exist, create it first
            this.createContainer();
            if (this.container) {
                this.container.appendChild(notification);
            } else {
                console.error('Failed to create notification container');
                return null;
            }
        }
        
        this.notifications.push(notification);
        
        // Trigger animation
        setTimeout(() => {
            notification.classList.add('show');
        }, 10);
        
        // Auto remove
        if (config.duration > 0) {
            setTimeout(() => {
                this.hide(notification);
            }, config.duration);
        }
        
        return notification;
    }
    
    createNotification(message, config) {
        const notification = document.createElement('div');
        notification.className = `notification ${config.type}`;
        
        const icon = this.getIcon(config.type);
        const progressBar = config.duration > 0 ? this.createProgressBar(config.duration) : '';
        
        notification.innerHTML = `
            <div class="notification-header">
                <div style="display: flex; align-items: center;">
                    ${icon}
                    <h4 class="notification-title">${config.title}</h4>
                </div>
                ${config.showCloseButton ? `
                    <button class="notification-close" onclick="window.notificationSystem.hide(this.closest('.notification'))">
                        <svg width="16" height="16" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
                        </svg>
                    </button>
                ` : ''}
            </div>
            <p class="notification-message">${message}</p>
            ${config.actions.length > 0 ? this.createActions(config.actions) : ''}
            ${progressBar}
        `;
        
        return notification;
    }
    
    getIcon(type) {
        const icons = {
            success: `<svg class="notification-icon text-green-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"></path>
            </svg>`,
            error: `<svg class="notification-icon text-red-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>
            </svg>`,
            warning: `<svg class="notification-icon text-yellow-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-2.5L13.732 4c-.77-.833-1.964-.833-2.732 0L3.732 16.5c-.77.833.192 2.5 1.732 2.5z"></path>
            </svg>`,
            info: `<svg class="notification-icon text-blue-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>
            </svg>`
        };
        return icons[type] || icons.info;
    }
    
    getDefaultTitle(type) {
        const titles = {
            success: 'Success',
            error: 'Error',
            warning: 'Warning',
            info: 'Information'
        };
        return titles[type] || 'Notification';
    }
    
    createProgressBar(duration) {
        return `
            <div class="notification-progress">
                <div class="notification-progress-bar" style="width: 100%; transition: width ${duration}ms linear;"></div>
            </div>
        `;
    }
    
    createActions(actions) {
        return `
            <div class="notification-actions">
                ${actions.map(action => `
                    <button class="notification-btn ${action.primary ? 'notification-btn-primary' : 'notification-btn-secondary'}" 
                            onclick="window.notificationSystem.handleAction('${action.id}', this.closest('.notification'))">
                        ${action.text}
                    </button>
                `).join('')}
            </div>
        `;
    }
    
    handleAction(actionId, notification) {
        // This will be handled by the onAction callback
        const actionIndex = this.notifications.indexOf(notification);
        if (actionIndex !== -1) {
            // Trigger action callback if provided
            this.hide(notification);
        }
    }
    
    hide(notification) {
        if (!notification) return;
        
        notification.classList.remove('show');
        notification.classList.add('hide');
        
        setTimeout(() => {
            if (notification.parentNode) {
                notification.parentNode.removeChild(notification);
            }
            const index = this.notifications.indexOf(notification);
            if (index !== -1) {
                this.notifications.splice(index, 1);
            }
        }, 400);
    }
    
    hideAll() {
        this.notifications.forEach(notification => {
            this.hide(notification);
        });
    }
    
    // Convenience methods
    success(message, options = {}) {
        return this.show(message, { ...options, type: 'success' });
    }
    
    error(message, options = {}) {
        return this.show(message, { ...options, type: 'error' });
    }
    
    warning(message, options = {}) {
        return this.show(message, { ...options, type: 'warning' });
    }
    
    info(message, options = {}) {
        return this.show(message, { ...options, type: 'info' });
    }
    
    // Specialized notifications
    showBookingSuccess(bookingId) {
        return this.success(
            `Your booking has been confirmed! Booking ID: ${bookingId}`,
            {
                title: 'Booking Confirmed',
                duration: 6000,
                actions: [
                    { id: 'view', text: 'View Booking', primary: true },
                    { id: 'close', text: 'Close', primary: false }
                ]
            }
        );
    }
    
    showPaymentSuccess(amount) {
        return this.success(
            `Payment of ₹${amount} has been processed successfully!`,
            {
                title: 'Payment Successful',
                duration: 5000
            }
        );
    }
    
    showNetworkError() {
        return this.error(
            'Network error occurred. Please check your connection and try again.',
            {
                title: 'Connection Error',
                duration: 0, // Don't auto-hide
                actions: [
                    { id: 'retry', text: 'Retry', primary: true },
                    { id: 'close', text: 'Close', primary: false }
                ]
            }
        );
    }
    
    showSeatExpired() {
        return this.warning(
            'Your seat selection has expired. Please select seats again.',
            {
                title: 'Seat Lock Expired',
                duration: 0,
                actions: [
                    { id: 'reselect', text: 'Select Again', primary: true },
                    { id: 'close', text: 'Close', primary: false }
                ]
            }
        );
    }
}

// Create global instance with proper initialization
document.addEventListener('DOMContentLoaded', function() {
    if (!window.notificationSystem) {
        window.notificationSystem = new NotificationSystem();
        console.log('Notification system initialized on DOM ready');
    }
});

// Also try immediate initialization as fallback
if (!window.notificationSystem) {
    window.notificationSystem = new NotificationSystem();
    console.log('Notification system initialized immediately');
}

// Export for module systems
if (typeof module !== 'undefined' && module.exports) {
    module.exports = NotificationSystem;
}
