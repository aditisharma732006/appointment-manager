import React from 'react';
import { clsx } from 'clsx';
import { twMerge } from 'tailwind-merge';

export function cn(...inputs) {
  return twMerge(clsx(inputs));
}

export const Button = React.forwardRef(({ className, variant = 'primary', size = 'default', ...props }, ref) => {
  const variants = {
    primary: 'bg-[#6366f1] text-white hover:bg-[#4f46e5] shadow-sm',
    danger: 'bg-[#ef4444] text-white hover:bg-[#dc2626] shadow-sm',
    outline: 'border-2 border-[#6366f1] text-[#6366f1] hover:bg-[#6366f1] hover:text-white',
    ghost: 'hover:bg-gray-100 text-gray-700'
  };

  const sizes = {
    default: 'h-10 px-4 py-2',
    sm: 'h-8 px-3 text-sm',
    lg: 'h-12 px-8 text-lg',
  };

  return (
    <button
      ref={ref}
      className={cn(
        'inline-flex items-center justify-center rounded-xl font-medium transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-[#6366f1] disabled:pointer-events-none disabled:opacity-50',
        variants[variant],
        sizes[size],
        className
      )}
      {...props}
    />
  );
});

Button.displayName = 'Button';
