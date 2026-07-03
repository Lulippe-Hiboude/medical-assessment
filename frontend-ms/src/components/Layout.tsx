interface Props {
    children: React.ReactNode;
}

export default function Layout({children}: Props) {
    return (
        <div className="min-h-screen bg-gray-100">
            <nav className="bg-white shadow px-6 py-4 flex items-center justify-between">
                <div className="flex items-center gap-3">
                    <img src="/logo.png" alt="Logo" className="h-8 w-8" />
                    <span className="text-x1 font-bold text-blue-700"> Medical Assessment</span>
                </div>
            </nav>
            <main className="max-w-full px-8 py-6">
                {children}
            </main>
        </div>
    )
}