import React from 'react';
import { cn } from './Button';

export function Badge({ className, variant = 'default', ...props }) {
  const variants = {
    default: 'bg-gray-100 text-gray-800',
    success: 'bg-[#22c55e] text-white',
    danger: 'bg-[#ef4444] text-white',
    warning: 'bg-[#f59e0b] text-white',
    primary: 'bg-[#6366f1] text-white',
  };

  return (
    <div
      className={cn(
        'inline-flex items-center rounded-full px-2.5 py-0.5 text-xs font-semibold transition-colors focus:outline-none focus:ring-2 focus:ring-ring focus:ring-offset-2',
        variants[variant],
        className
      )}
      {...props}
    />
  );
}
