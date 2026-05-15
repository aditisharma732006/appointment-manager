import React from 'react';
import { cn } from './Button';

export const Input = React.forwardRef(({ className, type, ...props }, ref) => {
  return (
    <input
      type={type}
      className={cn(
        'flex h-10 w-full rounded-xl border border-gray-300 bg-white px-3 py-2 text-sm placeholder:text-gray-400 focus:outline-none focus:ring-2 focus:ring-[#6366f1] focus:border-transparent disabled:cursor-not-allowed disabled:opacity-50 transition-shadow',
        className
      )}
      ref={ref}
      {...props}
    />
  );
});

Input.displayName = 'Input';
