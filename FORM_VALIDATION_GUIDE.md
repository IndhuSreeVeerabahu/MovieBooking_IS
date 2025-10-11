# Form Validation System Guide

## Overview

This movie ticket booking application now includes a comprehensive form validation system that provides real-time feedback to users, ensuring data integrity and improving user experience.

## Features

### ✅ Real-time Validation
- **On Blur**: Validates fields when user leaves the field
- **On Input**: Clears errors as user types
- **On Submit**: Comprehensive form validation before submission

### ✅ Visual Feedback
- **Error States**: Red borders and error messages for invalid fields
- **Success States**: Green borders for valid fields
- **Animations**: Shake animation for errors, smooth transitions
- **Icons**: Visual indicators for field states

### ✅ Comprehensive Validation Rules
- **Required Fields**: Ensures all mandatory fields are filled
- **Length Validation**: Minimum and maximum character limits
- **Email Validation**: Proper email format checking
- **Phone Validation**: 10-digit phone number format
- **Password Validation**: Minimum 6 characters
- **Password Confirmation**: Ensures passwords match
- **Terms Acceptance**: Requires agreement to terms

## Implementation

### 1. Form Setup

To add validation to any form, simply add the `data-validation="true"` attribute:

```html
<form id="myForm" data-validation="true">
    <!-- form fields -->
</form>
```

### 2. Field Structure

Each field should follow this structure:

```html
<div>
    <label for="fieldName" class="block text-sm font-medium text-gray-300 mb-2">
        Field Label <span class="text-red-400">*</span>
    </label>
    <input type="text" 
           id="fieldName" 
           name="fieldName" 
           required
           minlength="2"
           maxlength="50"
           class="w-full px-4 py-3 bg-bms-light-gray border border-gray-600 rounded-lg text-white placeholder-gray-400 focus:outline-none focus:border-bms-red input-focus transition-all duration-200"
           placeholder="Enter value">
    <div id="fieldNameError" class="hidden mt-1 text-sm text-red-400"></div>
</div>
```

### 3. Validation Attributes

| Attribute | Purpose | Example |
|-----------|---------|---------|
| `required` | Makes field mandatory | `required` |
| `minlength` | Minimum character count | `minlength="6"` |
| `maxlength` | Maximum character count | `maxlength="50"` |
| `pattern` | Regex pattern validation | `pattern="[0-9]{10}"` |
| `type` | HTML5 input type | `type="email"` |

### 4. Error Message Elements

Each field needs a corresponding error message element:

```html
<div id="fieldNameError" class="hidden mt-1 text-sm text-red-400"></div>
```

## Forms with Validation

### 1. Registration Form (`/register`)
- **First Name**: Required, 2-50 characters
- **Last Name**: Required, 2-50 characters
- **Email**: Required, valid email format
- **Phone**: Optional, 10-digit format
- **Password**: Required, minimum 6 characters
- **Confirm Password**: Required, must match password
- **Terms**: Required checkbox

### 2. Login Form (`/login`)
- **Email**: Required, valid email format
- **Password**: Required, minimum 6 characters

### 3. Payment Form (Booking Modal)
- **Payment Method**: Required radio button selection

### 4. Search Form (Movies Page)
- **Search Query**: Optional, maximum 100 characters

## Validation Rules

### Email Validation
```javascript
/^[^\s@]+@[^\s@]+\.[^\s@]+$/
```

### Phone Validation
```javascript
/^[0-9]{10}$/
```

### Password Requirements
- Minimum 6 characters
- No maximum limit (can be added if needed)

## Custom Validation

You can add custom validation rules by passing them to the form validator:

```javascript
const customRules = {
    customField: (value, field) => {
        // Custom validation logic
        if (value.includes('forbidden')) {
            return {
                isValid: false,
                message: 'This value is not allowed'
            };
        }
        return { isValid: true };
    }
};

window.formValidator.initForm('myForm', customRules);
```

## Error Messages

### Default Messages
- **Required**: "{Field Name} is required"
- **Min Length**: "{Field Name} must be at least {N} characters long"
- **Max Length**: "{Field Name} must be no more than {N} characters long"
- **Email**: "Please enter a valid email address"
- **Phone**: "Please enter a valid 10-digit phone number"
- **Password**: "Password must be at least 6 characters long"
- **Password Match**: "Passwords do not match"
- **Terms**: "You must agree to the terms and conditions"

### Customizing Messages

You can customize error messages by modifying the `errorMessages` object in `form-validation.js`:

```javascript
this.errorMessages = {
    required: (fieldName) => `Please provide ${fieldName}`,
    // ... other messages
};
```

## Styling

### CSS Classes

| Class | Purpose |
|-------|---------|
| `.form-field` | Base field styling |
| `.form-field.valid` | Valid field state |
| `.form-field.invalid` | Invalid field state |
| `.form-field.shake` | Error shake animation |
| `.error-message` | Error message styling |
| `.success-message` | Success message styling |

### Color Scheme
- **Error**: Red (`#ef4444`)
- **Success**: Green (`#10b981`)
- **Focus**: Brand red (`#F84464`)

## Browser Compatibility

- **Modern Browsers**: Full support
- **IE11+**: Basic support (no animations)
- **Mobile**: Responsive design with touch-friendly validation

## Performance

- **Lightweight**: ~5KB minified
- **No Dependencies**: Pure JavaScript
- **Efficient**: Event delegation and minimal DOM manipulation

## Testing

### Manual Testing Checklist

1. **Required Fields**
   - [ ] Empty required fields show error
   - [ ] Filling required fields clears error

2. **Length Validation**
   - [ ] Fields below minimum length show error
   - [ ] Fields above maximum length show error
   - [ ] Valid length fields are accepted

3. **Format Validation**
   - [ ] Invalid email format shows error
   - [ ] Invalid phone format shows error
   - [ ] Valid formats are accepted

4. **Password Confirmation**
   - [ ] Mismatched passwords show error
   - [ ] Matching passwords are accepted

5. **Form Submission**
   - [ ] Invalid forms don't submit
   - [ ] Valid forms submit successfully
   - [ ] First invalid field gets focus

6. **Real-time Feedback**
   - [ ] Errors clear as user types
   - [ ] Validation occurs on blur
   - [ ] Visual feedback is immediate

## Troubleshooting

### Common Issues

1. **Validation not working**
   - Check if `data-validation="true"` is on form
   - Ensure `form-validation.js` is loaded
   - Verify field names match error element IDs

2. **Error messages not showing**
   - Check if error element exists with correct ID
   - Ensure error element has `hidden` class initially
   - Verify CSS is loaded

3. **Styling issues**
   - Ensure `validation.css` is loaded
   - Check for CSS conflicts
   - Verify Tailwind CSS is loaded

### Debug Mode

Enable debug logging by adding this to the console:

```javascript
window.formValidator.debug = true;
```

## Future Enhancements

### Planned Features
- [ ] Server-side validation integration
- [ ] Async validation (email uniqueness, etc.)
- [ ] File upload validation
- [ ] Date/time validation
- [ ] Credit card validation
- [ ] Multi-language support

### Customization Options
- [ ] Configurable validation rules
- [ ] Custom error message templates
- [ ] Theme customization
- [ ] Animation preferences

## Support

For issues or questions about the validation system:
1. Check this documentation
2. Review the browser console for errors
3. Test with different browsers
4. Verify all dependencies are loaded

---

**Last Updated**: December 2024
**Version**: 1.0.0
