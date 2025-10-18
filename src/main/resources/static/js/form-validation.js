/**
 * Comprehensive Form Validation Utility
 * Provides real-time validation for all forms in the application
 */

class FormValidator {
    constructor() {
        this.validationRules = {
            required: (value) => value.trim() !== '',
            minLength: (value, min) => value.length >= min,
            maxLength: (value, max) => value.length <= max,
            email: (value) => /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value),
            phone: (value) => /^[0-9]{10}$/.test(value),
            password: (value) => value.length >= 6,
            passwordMatch: (value, confirmValue) => value === confirmValue,
            terms: (checked) => checked
        };
        
        this.errorMessages = {
            required: (fieldName) => `${fieldName} is required`,
            minLength: (fieldName, min) => `${fieldName} must be at least ${min} characters long`,
            maxLength: (fieldName, max) => `${fieldName} must be no more than ${max} characters long`,
            email: () => 'Please enter a valid email address',
            phone: () => 'Please enter a valid 10-digit phone number',
            password: () => 'Password must be at least 6 characters long',
            passwordMatch: () => 'Passwords do not match',
            terms: () => 'You must agree to the terms and conditions'
        };
        
        this.fieldLabels = {
            firstName: 'First name',
            lastName: 'Last name',
            email: 'Email address',
            username: 'Email address',
            password: 'Password',
            confirmPassword: 'Confirm password',
            phoneNumber: 'Phone number',
            terms: 'Terms and conditions'
        };
    }
    
    /**
     * Initialize form validation
     * @param {string} formId - ID of the form to validate
     * @param {Object} customRules - Custom validation rules for specific fields
     */
    initForm(formId, customRules = {}) {
        const form = document.getElementById(formId);
        if (!form) return;
        
        const inputs = form.querySelectorAll('input, select, textarea');
        
        inputs.forEach(input => {
            // Add real-time validation on blur
            input.addEventListener('blur', (e) => this.validateField(e.target, customRules));
            
            // Clear errors on input
            input.addEventListener('input', (e) => this.clearError(e.target));
            
            // Special handling for password confirmation
            if (input.name === 'confirmPassword') {
                input.addEventListener('blur', (e) => this.validatePasswordMatch(e.target));
            }
        });
        
        // Form submission validation
        form.addEventListener('submit', (e) => {
            e.preventDefault();
            
            let isValid = true;
            inputs.forEach(input => {
                if (!this.validateField(input, customRules)) {
                    isValid = false;
                }
            });
            
            // Special validation for password confirmation
            const confirmPassword = form.querySelector('input[name="confirmPassword"]');
            if (confirmPassword && !this.validatePasswordMatch(confirmPassword)) {
                isValid = false;
            }
            
            if (isValid) {
                form.submit();
            } else {
                // Focus on first invalid field
                const firstError = form.querySelector('.border-red-500');
                if (firstError) {
                    firstError.focus();
                }
            }
        });
    }
    
    /**
     * Validate a single field
     * @param {HTMLElement} field - The field to validate
     * @param {Object} customRules - Custom validation rules
     * @returns {boolean} - Whether the field is valid
     */
    validateField(field, customRules = {}) {
        // Check if field exists and has required properties
        if (!field || !field.name) {
            console.warn('validateField called with invalid field:', field);
            return { isValid: false, message: 'Invalid field' };
        }
        
        const value = field.value.trim();
        const fieldName = field.name;
        const fieldType = field.type;
        const errorElement = document.getElementById(fieldName + 'Error');
        
        let isValid = true;
        let errorMessage = '';
        
        // Clear previous error styling
        this.clearFieldError(field);
        
        // Apply custom rules first
        if (customRules[fieldName]) {
            const customResult = customRules[fieldName](value, field);
            if (!customResult.isValid) {
                isValid = false;
                errorMessage = customResult.message;
            }
        }
        
        // Apply standard validation rules
        if (isValid) {
            // Required field validation
            if (field.hasAttribute('required') && !this.validationRules.required(value)) {
                isValid = false;
                errorMessage = this.errorMessages.required(this.getFieldLabel(fieldName));
            }
            // Length validation
            else if (value && field.hasAttribute('minlength')) {
                const minLength = parseInt(field.getAttribute('minlength'));
                if (!this.validationRules.minLength(value, minLength)) {
                    isValid = false;
                    errorMessage = this.errorMessages.minLength(this.getFieldLabel(fieldName), minLength);
                }
            }
            else if (value && field.hasAttribute('maxlength')) {
                const maxLength = parseInt(field.getAttribute('maxlength'));
                if (!this.validationRules.maxLength(value, maxLength)) {
                    isValid = false;
                    errorMessage = this.errorMessages.maxLength(this.getFieldLabel(fieldName), maxLength);
                }
            }
            // Email validation
            else if ((fieldName === 'email' || fieldName === 'username') && value && !this.validationRules.email(value)) {
                isValid = false;
                errorMessage = this.errorMessages.email();
            }
            // Phone validation
            else if (fieldName === 'phoneNumber' && value && !this.validationRules.phone(value)) {
                isValid = false;
                errorMessage = this.errorMessages.phone();
            }
            // Password validation
            else if (fieldName === 'password' && value && !this.validationRules.password(value)) {
                isValid = false;
                errorMessage = this.errorMessages.password();
            }
            // Terms validation
            else if (fieldName === 'terms' && fieldType === 'checkbox' && !this.validationRules.terms(field.checked)) {
                isValid = false;
                errorMessage = this.errorMessages.terms();
            }
        }
        
        // Display error
        if (!isValid) {
            this.showFieldError(field, errorMessage);
        }
        
        return isValid;
    }
    
    /**
     * Validate password match
     * @param {HTMLElement} confirmPasswordField - The confirm password field
     * @returns {boolean} - Whether passwords match
     */
    validatePasswordMatch(confirmPasswordField) {
        const passwordField = document.getElementById('password');
        if (!passwordField) return true;
        
        const confirmValue = confirmPasswordField.value;
        const passwordValue = passwordField.value;
        
        let isValid = true;
        let errorMessage = '';
        
        // Clear previous error styling
        this.clearFieldError(confirmPasswordField);
        
        if (confirmValue && !this.validationRules.passwordMatch(passwordValue, confirmValue)) {
            isValid = false;
            errorMessage = this.errorMessages.passwordMatch();
        }
        
        // Display error
        if (!isValid) {
            this.showFieldError(confirmPasswordField, errorMessage);
        }
        
        return isValid;
    }
    
    /**
     * Show field error
     * @param {HTMLElement} field - The field with error
     * @param {string} message - Error message
     */
    showFieldError(field, message) {
        const fieldName = field.name;
        const errorElement = document.getElementById(fieldName + 'Error');
        
        // Add error styling to field
        field.classList.remove('border-gray-600', 'border-green-500', 'form-field', 'valid');
        field.classList.add('border-red-500', 'form-field', 'invalid', 'shake');
        
        // Remove shake animation after it completes
        setTimeout(() => {
            field.classList.remove('shake');
        }, 500);
        
        // Show error message
        if (errorElement) {
            errorElement.textContent = message;
            errorElement.classList.remove('hidden');
        }
    }
    
    /**
     * Clear field error
     * @param {HTMLElement} field - The field to clear error from
     */
    clearFieldError(field) {
        // Check if field exists and has a name property
        if (!field || !field.name) {
            console.warn('clearFieldError called with invalid field:', field);
            return;
        }
        
        const fieldName = field.name;
        const errorElement = document.getElementById(fieldName + 'Error');
        
        // Remove error styling from field
        field.classList.remove('border-red-500', 'invalid', 'shake');
        field.classList.add('border-gray-600', 'form-field');
        
        // Hide error message
        if (errorElement) {
            errorElement.classList.add('hidden');
        }
    }
    
    /**
     * Show field success
     * @param {HTMLElement} field - The field that is valid
     */
    showFieldSuccess(field) {
        field.classList.remove('border-gray-600', 'border-red-500', 'invalid');
        field.classList.add('border-green-500', 'form-field', 'valid');
    }
    
    /**
     * Clear error on input
     * @param {Event} event - Input event
     */
    clearError(event) {
        // Check if event and target exist
        if (!event || !event.target) {
            console.warn('clearError called with invalid event:', event);
            return;
        }
        this.clearFieldError(event.target);
    }
    
    /**
     * Get field label
     * @param {string} fieldName - Name of the field
     * @returns {string} - Human-readable field label
     */
    getFieldLabel(fieldName) {
        return this.fieldLabels[fieldName] || fieldName;
    }
    
    /**
     * Validate entire form
     * @param {string} formId - ID of the form to validate
     * @returns {boolean} - Whether the form is valid
     */
    validateForm(formId) {
        const form = document.getElementById(formId);
        if (!form) return false;
        
        const inputs = form.querySelectorAll('input[required], select[required], textarea[required]');
        let isValid = true;
        
        inputs.forEach(input => {
            if (!this.validateField(input)) {
                isValid = false;
            }
        });
        
        return isValid;
    }
}

// Create global instance
window.formValidator = new FormValidator();

// Auto-initialize forms with data-validation attribute
document.addEventListener('DOMContentLoaded', function() {
    const forms = document.querySelectorAll('form[data-validation]');
    forms.forEach(form => {
        const formId = form.id;
        if (formId) {
            window.formValidator.initForm(formId);
        }
    });
});
