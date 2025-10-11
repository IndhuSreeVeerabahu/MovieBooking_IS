/**
 * Professional Modal System
 * Replaces browser alerts with custom, professional modals
 */

class ModalSystem {
    constructor() {
        this.activeModals = [];
        this.modalCounter = 0;
        this.init();
    }
    
    init() {
        // Create modal container if it doesn't exist
        if (!document.getElementById('modal-container')) {
            this.createModalContainer();
        }
        
        // Override global alert function
        this.overrideAlert();
        
        // Add keyboard event listeners
        document.addEventListener('keydown', (e) => this.handleKeyboard(e));
    }
    
    createModalContainer() {
        const container = document.createElement('div');
        container.id = 'modal-container';
        container.className = 'fixed inset-0 z-50 hidden';
        container.innerHTML = `
            <div class="modal-backdrop fixed inset-0 bg-black bg-opacity-50 transition-opacity duration-300"></div>
            <div class="modal-content-container fixed inset-0 flex items-center justify-center p-4">
                <div class="modal-content bg-bms-gray rounded-lg shadow-2xl max-w-md w-full mx-4 transform transition-all duration-300 scale-95 opacity-0">
                    <div class="modal-header p-6 pb-4">
                        <div class="flex items-center justify-between">
                            <div class="flex items-center space-x-3">
                                <div class="modal-icon w-10 h-10 rounded-full flex items-center justify-center">
                                    <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>
                                    </svg>
                                </div>
                                <h3 class="modal-title text-xl font-semibold text-white"></h3>
                            </div>
                            <button class="modal-close text-gray-400 hover:text-white transition-colors">
                                <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
                                </svg>
                            </button>
                        </div>
                    </div>
                    <div class="modal-body p-6 pt-0">
                        <p class="modal-message text-gray-300"></p>
                    </div>
                    <div class="modal-footer p-6 pt-4">
                        <div class="modal-buttons flex space-x-3 justify-end"></div>
                    </div>
                </div>
            </div>
        `;
        
        document.body.appendChild(container);
    }
    
    overrideAlert() {
        // Store original alert function
        window.originalAlert = window.alert;
        
        // Override alert with our modal
        window.alert = (message) => {
            this.showAlert(message);
        };
    }
    
    showAlert(message, options = {}) {
        const modalId = `modal-${++this.modalCounter}`;
        const config = {
            title: options.title || 'Information',
            message: message,
            type: options.type || 'info', // info, success, warning, error
            showCloseButton: options.showCloseButton !== false,
            autoClose: options.autoClose || false,
            autoCloseDelay: options.autoCloseDelay || 3000,
            buttons: options.buttons || [
                {
                    text: 'OK',
                    class: 'bg-bms-red hover:bg-red-600 text-white px-6 py-2 rounded-lg font-medium transition-colors',
                    action: 'close'
                }
            ],
            onClose: options.onClose || null,
            onConfirm: options.onConfirm || null
        };
        
        this.createModal(modalId, config);
        return this;
    }
    
    showConfirm(message, options = {}) {
        const modalId = `modal-${++this.modalCounter}`;
        const config = {
            title: options.title || 'Confirm Action',
            message: message,
            type: options.type || 'warning',
            showCloseButton: options.showCloseButton !== false,
            autoClose: false,
            buttons: options.buttons || [
                {
                    text: 'Cancel',
                    class: 'bg-gray-600 hover:bg-gray-700 text-white px-6 py-2 rounded-lg font-medium transition-colors',
                    action: 'close'
                },
                {
                    text: 'Confirm',
                    class: 'bg-bms-red hover:bg-red-600 text-white px-6 py-2 rounded-lg font-medium transition-colors',
                    action: 'confirm'
                }
            ],
            onClose: options.onClose || null,
            onConfirm: options.onConfirm || null
        };
        
        this.createModal(modalId, config);
        return this;
    }
    
    showSuccess(message, options = {}) {
        return this.showAlert(message, {
            ...options,
            title: options.title || 'Success',
            type: 'success'
        });
    }
    
    showError(message, options = {}) {
        return this.showAlert(message, {
            ...options,
            title: options.title || 'Error',
            type: 'error'
        });
    }
    
    showWarning(message, options = {}) {
        return this.showAlert(message, {
            ...options,
            title: options.title || 'Warning',
            type: 'warning'
        });
    }
    
    showLoading(message = 'Loading...', options = {}) {
        const modalId = `modal-${++this.modalCounter}`;
        const config = {
            title: options.title || 'Please Wait',
            message: message,
            type: 'loading',
            showCloseButton: false,
            autoClose: false,
            buttons: [],
            onClose: options.onClose || null
        };
        
        this.createModal(modalId, config);
        return modalId;
    }
    
    createModal(modalId, config) {
        const container = document.getElementById('modal-container');
        const modal = document.createElement('div');
        modal.id = modalId;
        modal.className = 'modal-wrapper fixed inset-0 z-50';
        
        // Get icon and colors based on type
        const typeConfig = this.getTypeConfig(config.type);
        
        modal.innerHTML = `
            <div class="modal-backdrop fixed inset-0 bg-black bg-opacity-50 transition-opacity duration-300"></div>
            <div class="modal-content-container fixed inset-0 flex items-center justify-center p-4">
                <div class="modal-content bg-bms-gray rounded-lg shadow-2xl max-w-md w-full mx-4 transform transition-all duration-300 scale-95 opacity-0 border border-gray-700">
                    <div class="modal-header p-6 pb-4">
                        <div class="flex items-center justify-between">
                            <div class="flex items-center space-x-3">
                                <div class="modal-icon w-10 h-10 rounded-full flex items-center justify-center ${typeConfig.iconBg}">
                                    ${typeConfig.icon}
                                </div>
                                <h3 class="modal-title text-xl font-semibold text-white">${config.title}</h3>
                            </div>
                            ${config.showCloseButton ? `
                                <button class="modal-close text-gray-400 hover:text-white transition-colors">
                                    <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
                                    </svg>
                                </button>
                            ` : ''}
                        </div>
                    </div>
                    <div class="modal-body p-6 pt-0">
                        <p class="modal-message text-gray-300">${config.message}</p>
                    </div>
                    <div class="modal-footer p-6 pt-4">
                        <div class="modal-buttons flex space-x-3 justify-end">
                            ${config.buttons.map(button => `
                                <button class="modal-button ${button.class}" data-action="${button.action}">
                                    ${button.text}
                                </button>
                            `).join('')}
                        </div>
                    </div>
                </div>
            </div>
        `;
        
        container.appendChild(modal);
        this.activeModals.push(modalId);
        
        // Show modal with animation
        this.showModal(modalId);
        
        // Add event listeners
        this.addModalEventListeners(modalId, config);
        
        // Auto close if specified
        if (config.autoClose) {
            setTimeout(() => {
                this.closeModal(modalId);
            }, config.autoCloseDelay);
        }
        
        return modalId;
    }
    
    getTypeConfig(type) {
        const configs = {
            info: {
                icon: `<svg class="w-6 h-6 text-blue-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>
                </svg>`,
                iconBg: 'bg-blue-500 bg-opacity-20'
            },
            success: {
                icon: `<svg class="w-6 h-6 text-green-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"></path>
                </svg>`,
                iconBg: 'bg-green-500 bg-opacity-20'
            },
            warning: {
                icon: `<svg class="w-6 h-6 text-yellow-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-2.5L13.732 4c-.77-.833-1.964-.833-2.732 0L3.732 16.5c-.77.833.192 2.5 1.732 2.5z"></path>
                </svg>`,
                iconBg: 'bg-yellow-500 bg-opacity-20'
            },
            error: {
                icon: `<svg class="w-6 h-6 text-red-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>
                </svg>`,
                iconBg: 'bg-red-500 bg-opacity-20'
            },
            loading: {
                icon: `<div class="animate-spin rounded-full h-6 w-6 border-b-2 border-bms-red"></div>`,
                iconBg: 'bg-bms-red bg-opacity-20'
            }
        };
        
        return configs[type] || configs.info;
    }
    
    addModalEventListeners(modalId, config) {
        const modal = document.getElementById(modalId);
        
        // Close button
        const closeBtn = modal.querySelector('.modal-close');
        if (closeBtn) {
            closeBtn.addEventListener('click', () => {
                this.closeModal(modalId);
                if (config.onClose) config.onClose();
            });
        }
        
        // Backdrop click
        const backdrop = modal.querySelector('.modal-backdrop');
        backdrop.addEventListener('click', () => {
            this.closeModal(modalId);
            if (config.onClose) config.onClose();
        });
        
        // Action buttons
        const buttons = modal.querySelectorAll('.modal-button');
        buttons.forEach(button => {
            button.addEventListener('click', () => {
                const action = button.dataset.action;
                
                if (action === 'close') {
                    this.closeModal(modalId);
                    if (config.onClose) config.onClose();
                } else if (action === 'confirm') {
                    this.closeModal(modalId);
                    if (config.onConfirm) config.onConfirm();
                }
            });
        });
    }
    
    showModal(modalId) {
        const modal = document.getElementById(modalId);
        const container = document.getElementById('modal-container');
        
        // Show container
        container.classList.remove('hidden');
        
        // Trigger animation
        setTimeout(() => {
            const backdrop = modal.querySelector('.modal-backdrop');
            const content = modal.querySelector('.modal-content');
            
            backdrop.classList.add('opacity-100');
            content.classList.remove('scale-95', 'opacity-0');
            content.classList.add('scale-100', 'opacity-100');
        }, 10);
    }
    
    closeModal(modalId) {
        const modal = document.getElementById(modalId);
        if (!modal) return;
        
        const backdrop = modal.querySelector('.modal-backdrop');
        const content = modal.querySelector('.modal-content');
        
        // Animate out
        backdrop.classList.remove('opacity-100');
        content.classList.remove('scale-100', 'opacity-100');
        content.classList.add('scale-95', 'opacity-0');
        
        // Remove modal after animation
        setTimeout(() => {
            modal.remove();
            this.activeModals = this.activeModals.filter(id => id !== modalId);
            
            // Hide container if no more modals
            if (this.activeModals.length === 0) {
                const container = document.getElementById('modal-container');
                container.classList.add('hidden');
            }
        }, 300);
    }
    
    closeAllModals() {
        this.activeModals.forEach(modalId => {
            this.closeModal(modalId);
        });
    }
    
    handleKeyboard(e) {
        if (e.key === 'Escape' && this.activeModals.length > 0) {
            const lastModal = this.activeModals[this.activeModals.length - 1];
            this.closeModal(lastModal);
        }
    }
    
    // Utility methods for common use cases
    showBookingConfirmation(bookingDetails) {
        return this.showSuccess(
            `Your booking has been confirmed! Booking ID: ${bookingDetails.id}`,
            {
                title: 'Booking Confirmed',
                autoClose: true,
                autoCloseDelay: 5000
            }
        );
    }
    
    showPaymentSuccess(amount) {
        return this.showSuccess(
            `Payment of ₹${amount} has been processed successfully!`,
            {
                title: 'Payment Successful',
                autoClose: true,
                autoCloseDelay: 4000
            }
        );
    }
    
    showSeatExpired() {
        return this.showWarning(
            'Your seat selection has expired. Please select seats again.',
            {
                title: 'Seat Lock Expired',
                buttons: [
                    {
                        text: 'OK',
                        class: 'bg-bms-red hover:bg-red-600 text-white px-6 py-2 rounded-lg font-medium transition-colors',
                        action: 'close'
                    }
                ]
            }
        );
    }
    
    showNetworkError() {
        return this.showError(
            'Network error occurred. Please check your connection and try again.',
            {
                title: 'Connection Error',
                buttons: [
                    {
                        text: 'Retry',
                        class: 'bg-bms-red hover:bg-red-600 text-white px-6 py-2 rounded-lg font-medium transition-colors',
                        action: 'confirm'
                    },
                    {
                        text: 'Cancel',
                        class: 'bg-gray-600 hover:bg-gray-700 text-white px-6 py-2 rounded-lg font-medium transition-colors',
                        action: 'close'
                    }
                ]
            }
        );
    }
}

// Create global instance
window.modalSystem = new ModalSystem();

// Export for module systems
if (typeof module !== 'undefined' && module.exports) {
    module.exports = ModalSystem;
}
